package com.sergewesley.forge.service.command;
import com.sergewesley.forge.service.command.api.Command;
import com.sergewesley.forge.external.chucknorris.ChuckNorrisService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JokeCommand implements Command {

    private final ChuckNorrisService chuckNorrisService;

    public JokeCommand(ChuckNorrisService chuckNorrisService) {
        this.chuckNorrisService = chuckNorrisService;
    }

    @Override
    public String getName() {
        return "joke";
    }

    @Override
    public String execute(String args) {
        Optional<String> jokeOpt = chuckNorrisService.getRandomJoke();
        return jokeOpt.orElse("Chuck Norris is too busy to tell a joke right now.");
    }

    @Override
    public String getDescription() {
        return "Get a random Chuck Norris joke.";
    }
}
