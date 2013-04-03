package de.unipotsdam.nexplorer.server.rest.dto;

import de.unipotsdam.nexplorer.server.rest.JSONable;
import de.unipotsdam.nexplorer.shared.GameStats;

/*
 * has the effect that GameStats becomes compatible with the JSON framework
 * 
 */
public class GameStatusJSON extends JSONable<GameStats> {

}
