package com.binancebot;

import com.binancebot.view.MainView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BinancebotApplication extends AbstractJavaFxApplicationSupport {
	public static void main(String[] args) {
		launchApp(BinancebotApplication.class, MainView.class, args);
	}
}
