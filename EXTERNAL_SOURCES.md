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

- **Ford/Charm: 2004 Explorer Sport Trac compressor service procedure** — https://charm.li/Ford/2004/Explorer%20Sport%20Trac%202WD%20V6-4.0L%20VIN%20K%20Flex%20Fuel/Repair%20and%20Diagnosis/Heating%20and%20Air%20Conditioning/Compressor%20HVAC/Service%20and%20Repair/
  - Used for the model’s conservative compressor/contamination/O-ring repair boundaries. Confirm the exact vehicle configuration, VIN, and under-hood label before physical service.
