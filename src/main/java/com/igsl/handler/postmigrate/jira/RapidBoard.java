package com.igsl.handler.postmigrate.jira;

import java.net.URI;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igsl.config.Config;
import com.igsl.export.cloud.CloudJiraBoards;
import com.igsl.handler.URLPattern;
import com.igsl.handler.postmigrate.BasePostMigrate;
import com.igsl.handler.postmigrate.MappingSetting;
import com.igsl.handler.postmigrate.ParamSetting;

public class RapidBoard extends BasePostMigrate {

	private static final Logger LOGGER = LogManager.getLogger(RapidBoard.class);
	private static final String BOARD_ID = "rapidView";
	
	private static final URLPattern[] PATTERNS = new URLPattern[] {
		new URLPattern().setPath("/secure/RapidBoard.jspa").setQuery("rapidView"),
	};
	
	public RapidBoard(Config config) {
		super(	config, 
				config.getUrlTransform().getConfluenceFromHost(), 
				Arrays.asList(
					new MappingSetting(
						new CloudJiraBoards(), 
						CloudJiraBoards.COL_DCID, 
						CloudJiraBoards.COL_CLOUDID)
				),
				null,
				Arrays.asList(
					new ParamSetting(RapidBoard.class, BOARD_ID, CloudJiraBoards.class)
				));
	}

	@Override
	protected boolean _accept(URI uri) {
		if (!super._accept(uri)) {
			return false;
		}
		for (URLPattern path : PATTERNS) {
			if (path.match(uri)) {
				return true;
			}
		}
		return false;
	}
}
