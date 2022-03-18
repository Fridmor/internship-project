package com.github.fridmor;

import com.github.fridmor.util.command.CommandExecutor;
import com.github.fridmor.util.command.CommandHandler;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HelloWorld {
    public static void main(String[] args) throws PythonExecutionException, IOException {
        CommandHandler commandHandler = new CommandHandler("rate AMD -date week -alg moon");
        CommandExecutor commandExecutor = new CommandExecutor(commandHandler);
        commandExecutor.execute();
        System.out.println(commandExecutor.getOutput());
    }
}
