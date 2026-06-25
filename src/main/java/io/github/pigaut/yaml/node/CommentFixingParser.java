package io.github.pigaut.yaml.node;

import org.snakeyaml.engine.v2.api.*;
import org.snakeyaml.engine.v2.comments.*;
import org.snakeyaml.engine.v2.events.*;
import org.snakeyaml.engine.v2.parser.*;
import org.snakeyaml.engine.v2.scanner.*;

public class CommentFixingParser implements Parser {
    private final Parser delegate;
    private Event.ID lastEventId = null;

    public CommentFixingParser(LoadSettings settings, StreamReader streamReader) {
        this.delegate = new ParserImpl(settings, streamReader);
    }

    @Override
    public Event peekEvent() {
        Event event = delegate.peekEvent();
        return wrapEvent(event);
    }

    @Override
    public Event next() {
        Event event = delegate.next();
        Event fixedEvent = wrapEvent(event);
        lastEventId = fixedEvent.getEventId();
        return fixedEvent;
    }

    private Event wrapEvent(Event event) {
        if (event instanceof CommentEvent ce && ce.getCommentType() == CommentType.IN_LINE) {
            if (lastEventId == Event.ID.DocumentStart || lastEventId == Event.ID.StreamStart) {
                return new CommentEvent(CommentType.BLOCK, ce.getValue(), ce.getStartMark(), ce.getEndMark());
            }
        }
        return event;
    }

    @Override
    public boolean checkEvent(Event.ID id) {
        return delegate.checkEvent(id);
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

}
