package me.loaidev.teams.exceptions;

import me.loaidev.core.exceptions.DisplayException;

public class NoTeamException extends DisplayException {

    public NoTeamException() {
        super("You are not in a team");
    }
}
