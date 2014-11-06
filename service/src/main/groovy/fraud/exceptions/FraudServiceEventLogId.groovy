package fraud.exceptions

import common.exceptions.EventLogBase
import common.exceptions.EventType

enum FraudServiceEventLogId implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    IDShouldNotBeNull(BASE_EVENT_LOG_ID, 400,
            'Transaction id should not be null.',
            EventType.InvalidInput),

    TransactionNotFound(BASE_EVENT_LOG_ID + 1, 400,
            'Transaction with id %1$l is not found.',
            EventType.InvalidInput),

    InvalidPageNumber(BASE_EVENT_LOG_ID + 2, 400,
            'Invalid page number provided: \'%1$s\'',
            EventType.InvalidInput),

    IsInBlackListAlready(BASE_EVENT_LOG_ID + 3, 400,
            '%1$s with value= \'%2$s\' is in black list already',
            EventType.InvalidInput),

    BlackListItemIsNotExist(BASE_EVENT_LOG_ID + 4, 400,
            '%1$s with value= \'%2$s\' is not exist',
            EventType.InvalidInput),

    VelocityConfigNotFound(BASE_EVENT_LOG_ID + 5, 404,
            'VelocityConfig for metric \'%1$s\' is not found.',
            EventType.InvalidInput),

    VelocityConfigAlreadyExists(BASE_EVENT_LOG_ID + 6, 400,
            'VelocityConfig for metric \'%1$s\' already exists.',
            EventType.InvalidInput),

    UnexpectedErrorDuringVelocityComputation(BASE_EVENT_LOG_ID + 7, 500,
            'Unexpected error during velocity metric \'%1$s\' computation.',
            EventType.InternalError);

    private static final int BASE_EVENT_LOG_ID = 20000;

    EventLogBase.Level logLevel;
    String formatString;
    int eventId;
    int responseId;
    EventType eventType;

    private FraudServiceEventLogId(
            final int eventId, final int responseId, final String formatString, final EventType eventType) {
        this(eventId, responseId, EventLogBase.Level.ERROR, formatString, eventType);
    }

    private FraudServiceEventLogId(
            final int eventId, final int responseId, final EventLogBase.Level level, final String formatString,
            final EventType eventType) {
        this.eventId = eventId;
        this.responseId = responseId;
        logLevel = level;
        this.formatString = formatString;
        this.eventType = eventType;
    }

    String getMessage(final Object... arguments) {
        arguments != null && arguments.length > 0 ? String.format(formatString, arguments) : formatString;
    }
}
