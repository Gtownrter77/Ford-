# External Sources for Parts Readiness and Purchase Routing

## Retailer access and checkout

- **O'Reilly Pro Online** — https://www.oreillyauto.com/for-the-professional
  - O'Reilly describes OReillyPro.com as a professional B2B e-commerce service with VIN information, part-number interchange, availability lookup, saved quotes, and electronic orders to the servicing store. The app should use an O'Reilly Pro handoff until account-authorized integration is available.

- **eBay Browse API** — https://developer.ebay.com/develop/api/buy/browse_api
  - Supports listing discovery by keyword, product, category, and compatibility criteria, including item compatibility checking. Requires application access.

- **eBay Order API** — https://developer.ebay.com/api-docs/buy/static/api-order.html
  - Supports member and guest checkout sessions but is limited release in production. Any direct in-app checkout must use authorized eBay access and customer confirmation; a retailer handoff remains the default.

- **Amazon Creators API** — https://affiliate-program.amazon.com/creatorsapi/docs/en-us/introduction
  - Provides product-catalog discovery operations for enrolled Amazon Associates publishers/partners. It requires API registration and credentials; it does not justify an unapproved scraping implementation.

## A/C service safety

- **EPA: Handling Contaminated Automotive Refrigerants** — https://www.epa.gov/mvac/handling-contaminated-automotive-refrigerants
  - Vehicles should have a refrigerant identification label. Contaminated or unfamiliar automotive refrigerant must be recovered before repair or recharge; venting automotive refrigerants is prohibited except carbon dioxide/R-744. The app’s seasonal A/C alert therefore prompts inspection and documented service preparation, not automatic refrigerant charging.

## Workshop manual (CHARM) — 2004 Sport Trac 4WD V6-4.0L VIN K Flex Fuel

- **Hub** — https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/
- **Repair and Diagnosis** — https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/
- **All DTCs** — https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/A%20L%20L%20%20Diagnostic%20Trouble%20Codes%20%28%20DTC%20%29/
- **Parts and Labor** — https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Parts%20and%20Labor/
- **A/C compressor service (4WD VIN K)** — https://charm.li/Ford/2004/Explorer%20Sport%20Trac%204WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/Heating%20and%20Air%20Conditioning/Compressor%20HVAC/Service%20and%20Repair/
- Index file: `docs/2004_SPORT_TRAC_CHARM_WORKSHOP.md`
- Older 2WD compressor URL kept only as a prior citation; 4WD truck work uses the 4WD hub above.
- **2WD compressor citation (do not use for 4WD truck)** — https://charm.li/Ford/2004/Explorer%20Sport%20Trac%202WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/Heating%20and%20Air%20Conditioning/Compressor%20HVAC/Service%20and%20Repair/

## Official 2004 owner publications

- **Ford owner-manual hub** — https://www.ford.com/support/owner-manuals-details/explorer-sport-trac/2004
- **Owner Guide part 1** — https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og1e.pdf
- **Owner Guide part 2** — https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og2e.pdf
- **Owner Guide part 3** — https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27og3e.pdf
- **Quick Reference Guide** — https://www.fordservicecontent.com/Ford_Content/catalog/owner_guides/04p27qg1e.pdf
- Extracted tables: `docs/2004_SPORT_TRAC_OWNER_GUIDE_SPECS.md`
- Scope: fluids, Motorcraft service parts, capacities, engine data, dimensions, lug-nut torque. Not workshop pinpoint tests or repair torque sequences.

## Community and aftermarket references (not factory spec)

- **Ford Explorer Forums** — https://www.explorerforum.com/forums/
- **2001-2005 Explorer Sport Trac board (this truck)** — https://www.explorerforum.com/forums/forums/2001-2005-explorer-sport-trac.120/
- **2007-2010 Explorer Sport Trac board (do not mix)** — https://www.explorerforum.com/forums/forums/2007-2010-explorer-sport-trac.124/
- Use: owner failure patterns and real-truck notes only. Do not override Owner Guide or CHARM values.
- **Haynes aftermarket manual (Storer, 2005; Sport Trac through 2005)** — https://archive.org/details/fordexplorermazd0000stor_x6g5
  - ISBN 1563925915. Print-disabled / borrow item. Not the factory WSM.
