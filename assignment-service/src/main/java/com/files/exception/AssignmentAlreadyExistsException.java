package com.files.exception;
public class AssignmentAlreadyExistsException extends RuntimeException {

    public AssignmentAlreadyExistsException(String ticketId) {
        super("Ticket already assigned: " + ticketId);
    }
}
