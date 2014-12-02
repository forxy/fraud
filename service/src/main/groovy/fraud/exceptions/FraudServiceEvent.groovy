package fraud.exceptions

import common.exceptions.EventLogBase

import static common.exceptions.EventLogBase.*
import static common.exceptions.EventLogBase.EventType.*
import static common.exceptions.EventLogBase.Level

enum FraudServiceEvent implements EventLogBase {

    // -------------------------------------------------------------------
    // Business events
    // -------------------------------------------------------------------
    IDShouldNotBeNull(0, 400, InvalidInput,
            'Transaction id should not be null.'),

    TransactionNotFound(1, 400, InvalidInput,
            'Transaction with id %1$l is not found.'),

    InvalidPageNumber(2, 400, InvalidInput,
            'Invalid page number provided: \'%1$s\''),

    IsInBlackListAlready(3, 400, InvalidInput,
            '%1$s with value=\'%2$s\' is in black list already'),

    BlackListItemIsNotExist(4, 400, InvalidInput,
            '%1$s with value=\'%2$s\' is not exist'),

    VelocityConfigNotFound(5, 404, InvalidInput,
            'VelocityConfig for metric \'%1$s\' is not found.'),

    VelocityConfigAlreadyExists(6, 400, InvalidInput,
            'VelocityConfig for metric \'%1$s\' already exists.'),

    UnexpectedErrorDuringVelocityComputation(7, 500, EventType.InternalError,
            'Unexpected error during velocity metric \'%1$s\' computation.');


    private static int BASE_EVENT_LOG_ID = 20000;

    private FraudServiceEvent(int eventId, int responseId, EventType eventType, String formatString) {
        this(eventId, responseId, Level.ERROR, eventType, formatString);
    }

    private FraudServiceEvent(int eventID, int responseId, Level logLevel, EventType eventType, String formatString) {
        this.eventID = BASE_EVENT_LOG_ID + eventID;
        this.responseID = responseId;
        this.logLevel = logLevel;
        this.formatString = formatString;
        this.eventType = eventType;
    }
}
