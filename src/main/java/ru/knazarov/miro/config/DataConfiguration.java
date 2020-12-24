package ru.knazarov.miro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.knazarov.miro.model.Widget;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@Configuration
public class DataConfiguration {

    @Bean
    NavigableMap<Integer, Widget> widgetList(){
        return new TreeMap<>();
    }

    @Bean
    Map<String, Integer> zIndexList()
    {
        return new HashMap<>();
    }
}
