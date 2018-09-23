package com.example.main;

import com.example.config.Config;
import com.example.core.Core;
import com.example.endpoints.Controller1;
import com.example.io.Client;

public class App {
    public static void main(String[] argv){
        final Config config = new Config("foo","bar");
        final Core core = new Core(config.getFoo(),123);
        final Controller1 controller1 = new Controller1(core);
        final Client client = new Client(core);
    }
}
