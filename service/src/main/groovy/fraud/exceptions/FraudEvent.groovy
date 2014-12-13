package fraud.exceptions

import common.exceptions.EventLogBase

enum FraudEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    IDShouldNotBeNull(0, 400, EventLogBase.EventType.InvalidInput,
            'Transaction id should not be null.'),

    TransactionNotFound(1, 400, EventLogBase.EventType.InvalidInput,
            'Transaction with id %1$l is not found.'),

    InvalidPageNumber(2, 400, EventLogBase.EventType.InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    IsInBlackListAlready(3, 400, EventLogBase.EventType.InvalidInput,
            '%1$s with value=\'%2$s\' is in black list already'),

    BlackListItemIsNotExist(4, 400, EventLogBase.EventType.InvalidInput,
            '%1$s with value=\'%2$s\' is not exist'),

    VelocityConfigNotFound(5, 404, EventLogBase.EventType.InvalidInput,
            'VelocityConfig for metric \'%1$s\' is not found.'),

    VelocityConfigAlreadyExists(6, 400, EventLogBase.EventType.InvalidInput,
            'VelocityConfig for metric \'%1$s\' already exists.'),

    UnexpectedErrorDuringVelocityComputation(7, 500, EventLogBase.EventType.InternalError,
            'Unexpected error during velocity metric \'%1$s\' computation.')

    private static int BASE_EVENT_LOG_ID = 20000

    private FraudEvent(int eventId, int responseId, EventLogBase.EventType eventType, String formatString) {
        this(eventId, responseId, EventLogBase.Level.ERROR, eventType, formatString)
    }

    private FraudEvent(int eventID, int httpCode, EventLogBase.Level logLevel, EventLogBase.EventType eventType,
                       String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID
        this.httpCode = httpCode
        this.logLevel = logLevel
        this.formatString = formatString
        this.eventType = eventType
    }
}
