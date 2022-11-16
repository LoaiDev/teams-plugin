package me.loaidev.teams.exceptions;

import me.loaidev.core.exceptions.DisplayException;

public class HasTeamException extends DisplayException {

    public HasTeamException() {
        super("You already have a team.");
    }
}
