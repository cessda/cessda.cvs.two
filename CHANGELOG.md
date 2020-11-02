# Changelog

All notable changes to this project will be documented in this file.

## [2.0.0] - 2020-11-02

The first version of CESSDA Vocabulary Service (CVS) 2, which was re-developed from scratch based on the deprecated CVS 1 application.    
The project uses new architecture Spring-Boot framework for the back-end and Angular for the front-end, instead of Vaadin like in CVS 1.

### Added

- Back-end uses Spring-Boot v 2.2.5.RELEASE.
- Front-end uses Angular v 10.1.5.
- Improve Docker support, compare to CVS 1.
- Responsive web design
- Improved styling and looks-and-feels
- JSON Web Token (JWT) authentication, a stateless security mechanism. In order to improve scalability.
- Support for 28 Controlled-Vocabularies (CVs) languages (Albanian (sq), Bosnian (bs), Bulgarian (bg), Croatian (hr), Czech (cs), Danish (da), Dutch (nl), English (en), Estonian (et), Finnish (fi), French (fr), German (de), Greek (el), Hungarian (hu), Italian (it), Japanese (ja), Lithuanian (lt), Macedonian (mk), Norwegian (no), Polish (pl), Portuguese (pt), Romanian (ro), Russian (ru), Serbian (sr), Slovak (sk), Slovenian (sl), Spanish (es), Swedish (sv))
- Fixed corrupted CVs from the CVS 1
- Fixed and improved Vocabulary workflow
- Fixed vocabulary cloning and versioning 
- Store published-vocabulary in JSON format
- New URLs for both Publication and Editor pages, for better readability
- Bookmarkable URLs for searching and vocabulary
- Vocabulary and Codes searching with Elastic-Search
- New redesign Search UI, search Vocabulary based on specific language
- Search filters (e.g based on Agency) with [ngx-chips](https://www.npmjs.com/package/ngx-chips) component.
- Optimized and fixed searching functionality from CVS1
- Compare vocabulary versions with [ngx-text-diff](https://www.npmjs.com/package/ngx-text-diff) component, which based on `google diff match patch` library.
- Responsive Tree component adapts screen-size, to show the Vocabulary codes hierarchical structure.
- Vocabulary version comments functionality.
- Rich-Textarea with [ngx-quill](https://www.npmjs.com/package/ngx-quill) component.
- Agency page, with agency detail and statistic.
- Charts library using [ngx-charts](https://swimlane.gitbook.io/ngx-charts/) component.
- Admin page with user-management and licence-management.
- Application technical tools and matrices such as JVM-Metrics, App Health Checks, Configuration, Logs Audit.
- Swagger UI for the APIs.
- New set of /v2 REST APIs for searching and exporting CVs to several formats (PDF, HTML, DOCX)


