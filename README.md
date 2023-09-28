# Confluence URL Path
This tool is used to fix URLs before using Confluence Cloud Migration Assistant (CCMA).

CCMA converts URLs inserted using Search/Recently Viewed/Files. Those links are relative links pointing to Confluence content. 
However CCMA does not convert URLs inserted using Web Link or Advanced. Those links are absolute links.

## Configuration
Configuration is stored in Config.json. See com.igsl.config.Config for explanation of each property.

## How to Run
1. TODO

## URL Conversion Logic
1. Query Confluence database to retrieve latest version of all pages.
1. For each page: 
    1. Extract URLs (href="...") from content of each page.
    1. For each URL: 
        1. For each handler configured, URL is checked for acceptance: 
            1. Scheme (use default scheme if there is none)
            2. Domain
            3. Path or query parameter
        1. If URL is accepted by an handler, no other handlers will be invoked.
        1. Handler calculates a new URL.
        1. If performUpdate is true, new content body is saved to database.