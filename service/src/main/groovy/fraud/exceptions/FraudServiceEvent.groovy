package fraud.exceptions

import common.exceptions.EventLogBase
import common.exceptions.EventType

import static common.exceptions.EventLogBase.Level

enum FraudServiceEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    IDShouldNotBeNull(0, 400, EventType.InvalidInput,
            'Transaction id should not be null.'),

    TransactionNotFound(1, 400, EventType.InvalidInput,
            'Transaction with id %1$l is not found.'),

    InvalidPageNumber(2, 400, EventType.InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    IsInBlackListAlready(3, 400, EventType.InvalidInput,
            '%1$s with value= \'%2$s\' is in black list already'),

    BlackListItemIsNotExist(4, 400, EventType.InvalidInput,
            '%1$s with value= \'%2$s\' is not exist'),

    VelocityConfigNotFound(5, 404, EventType.InvalidInput,
            'VelocityConfig for metric \'%1$s\' is not found.'),

    VelocityConfigAlreadyExists(6, 400, EventType.InvalidInput,
            'VelocityConfig for metric \'%1$s\' already exists.'),

    UnexpectedErrorDuringVelocityComputation(7, 500, EventType.InternalError,
            'Unexpected error during velocity metric \'%1$s\' computation.');


    private static final int BASE_EVENT_LOG_ID = 20000;

    private FraudServiceEvent(final int eventId, final int responseId, final EventType eventType,
                              final String formatString) {
        this(eventId, responseId, Level.ERROR, eventType, formatString);
    }

    private FraudServiceEvent(final int eventID, final int responseId, final Level logLevel, final EventType eventType,
                              final String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID;
        this.responseID = responseId;
        this.logLevel = logLevel;
        this.formatString = formatString;
        this.eventType = eventType;
    }
}
