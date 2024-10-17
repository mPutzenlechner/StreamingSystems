package org.example.commands;
import org.example.events.IEvent;

public interface ICommand {
    public IEvent resolve();
    public Exception reject(String reason) throws Exception;
}
