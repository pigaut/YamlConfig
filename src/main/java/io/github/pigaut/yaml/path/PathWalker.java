package io.github.pigaut.yaml.path;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.node.*;
import io.github.pigaut.yaml.node.scalar.*;
import io.github.pigaut.yaml.node.section.*;
import io.github.pigaut.yaml.node.sequence.*;
import io.github.pigaut.yaml.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public final class PathWalker {

    private static final Pattern DOT_SPLIT = Pattern.compile("\\.");

    /** One dot-separated piece of the path: its alias names plus trailing indices, e.g. "a|b[0][1]". */
    private record Segment(List<String> names, List<IndexKey> indices) {}

    /** The branch holding the final key, and that key. */
    private record Location(Branch parent, FieldKey key) {}

    /** A branch we already reached, and the next segment still to be resolved from it. */
    private record Anchor(Branch branch, int next) {}

    private final Branch root;
    private final List<Segment> segments;

    private PathWalker(@NotNull Branch root, @NotNull List<Segment> segments) {
        Preconditions.checkArgument(!segments.isEmpty(), "Path must have at least one segment");
        this.root = root;
        this.segments = segments;
    }

    public static @NotNull PathWalker ofPath(@NotNull Branch root, @NotNull String path) {
        return new PathWalker(root, parse(path));
    }

    /** The key is used as is: no splitting on "." or "|" and no index parsing. */
    public static @NotNull PathWalker ofKey(@NotNull Branch root, @NotNull String literalKey) {
        return new PathWalker(root, List.of(new Segment(List.of(literalKey), List.of())));
    }

    // ---------- read-only ----------

    /** The field at the end of the path, or null if it doesn't exist. */
    public @Nullable ConfigField find() {
        Location location = locate(root, 0);
        return location == null ? null : location.key().getField(location.parent());
    }

    public boolean remove() {
        Location location = locate(root, 0);
        if (location == null) return false;
        location.key().removeField(location.parent());
        return true;
    }

    public @NotNull Scalar createScalar(@NotNull Object value, boolean attached) {
        Location location = locateForCreate(attached);
        return location.key().createScalar(location.parent(), value, attached);
    }

    public @NotNull Section createSection(boolean attached) {
        Location location = locateForCreate(attached);
        return branchOrCreate(location.parent(), location.key(), false, attached).convertToSection();
    }

    public @NotNull Sequence createSequence(boolean attached) {
        Location location = locateForCreate(attached);
        return branchOrCreate(location.parent(), location.key(), true, attached).convertToSequence();
    }

    /**
     * Finds an existing field matching segments[start..], trying every way of joining
     * consecutive segments into one key. Returns null if nothing matches.
     */
    private @Nullable Location locate(Branch branch, int start) {
        List<String> candidates = List.of();
        for (int end = start; end < segments.size(); end++) {
            Segment segment = segments.get(end);

            if (end == start) {
                candidates = segment.names();
            } else if (segment.names().isEmpty()) {
                break; // a nameless "[0]" segment can't be part of a dotted key
            } else {
                candidates = join(candidates, segment.names());
            }

            Location found = tryChain(branch, chain(candidates, segment.indices()), end);
            if (found != null) return found;

            if (!segment.indices().isEmpty()) {
                break; // indices always end a key
            }
        }
        return null;
    }

    private @Nullable Location tryChain(Branch branch, List<FieldKey> chain, int end) {
        Branch parent = branch;
        for (int i = 0; i < chain.size() - 1; i++) {
            if (!(chain.get(i).getField(parent) instanceof Branch next)) return null;
            parent = next;
        }

        FieldKey last = chain.get(chain.size() - 1);
        ConfigField field = last.getField(parent);
        if (field == null) return null;

        if (end == segments.size() - 1) {
            return new Location(parent, last);
        }
        return field instanceof Branch next ? locate(next, end + 1) : null;
    }

    /**
     * If the full path exists, returns it. Otherwise, reuses the deepest existing branch
     * (which may be reached through dotted keys) and creates the remaining parents.
     */
    private Location locateForCreate(boolean attached) {
        Location existing = locate(root, 0);
        if (existing != null) return existing;

        Anchor anchor = deepestAnchor(root, 0);
        List<FieldKey> rest = flatten(anchor.next());

        Branch current = anchor.branch();
        for (int i = 0; i < rest.size() - 1; i++) {
            boolean asSequence = rest.get(i + 1) instanceof IndexKey;
            current = branchOrCreate(current, rest.get(i), asSequence, attached);
        }
        return new Location(current, rest.get(rest.size() - 1));
    }

    /** Deepest already-existing branch reachable by consuming segments, never the last one. */
    private Anchor deepestAnchor(Branch branch, int start) {
        Anchor best = new Anchor(branch, start);
        List<String> candidates = List.of();

        for (int end = start; end < segments.size() - 1; end++) {
            Segment segment = segments.get(end);

            if (end == start) {
                candidates = segment.names();
            } else if (segment.names().isEmpty()) {
                break;
            } else {
                candidates = join(candidates, segment.names());
            }

            Branch reached = walkBranches(branch, chain(candidates, segment.indices()));
            if (reached != null) {
                Anchor deeper = deepestAnchor(reached, end + 1);
                if (deeper.next() > best.next()) best = deeper;
            }

            if (!segment.indices().isEmpty()) break;
        }
        return best;
    }

    private static @Nullable Branch walkBranches(Branch branch, List<FieldKey> chain) {
        Branch current = branch;
        for (FieldKey key : chain) {
            if (!(key.getField(current) instanceof Branch next)) return null;
            current = next;
        }
        return current;
    }

    private static Branch branchOrCreate(Branch parent, FieldKey key, boolean asSequence, boolean attached) {
        if (key.getField(parent) instanceof Branch existing) {
            return existing;
        }
        return asSequence ? key.createSequence(parent, attached) : key.createSection(parent, attached);
    }

    private static List<Segment> parse(String path) {
        List<Segment> segments = new ArrayList<>();
        for (String raw : DOT_SPLIT.split(path)) {
            Matcher matcher = PathFormatter.INDEX_PATTERN.matcher(raw);
            List<IndexKey> indices = new ArrayList<>();
            while (matcher.find()) {
                indices.add(IndexKey.fromString(matcher.group(1)));
            }

            String name = matcher.reset().replaceAll("");
            List<String> names = name.isEmpty() ? List.of() : List.of(name.split("\\|"));

            Preconditions.checkArgument(!names.isEmpty() || !indices.isEmpty(), "Path contains an empty key");
            segments.add(new Segment(names, indices));
        }
        return segments;
    }

    /** Every combination of one name per segment, joined with ".". */
    private static List<String> join(List<String> prefixes, List<String> names) {
        List<String> joined = new ArrayList<>(prefixes.size() * names.size());
        for (String prefix : prefixes) {
            for (String name : names) {
                joined.add(prefix + "." + name);
            }
        }
        return joined;
    }

    private static FieldKey nameKey(List<String> names) {
        return names.size() == 1 ? new LiteralKey(names.get(0)) : new MultiKey(names.toArray(new String[0]));
    }

    /** [name key (if any)] followed by the index keys. */
    private static List<FieldKey> chain(List<String> names, List<IndexKey> indices) {
        List<FieldKey> chain = new ArrayList<>(indices.size() + 1);
        if (!names.isEmpty()) chain.add(nameKey(names));
        chain.addAll(indices);
        return chain;
    }

    /** One key per name and index for segments[from..], used when creating new structure. */
    private List<FieldKey> flatten(int from) {
        List<FieldKey> keys = new ArrayList<>();
        for (int i = from; i < segments.size(); i++) {
            keys.addAll(chain(segments.get(i).names(), segments.get(i).indices()));
        }
        return keys;
    }

}