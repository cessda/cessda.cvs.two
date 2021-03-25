

# Changelog

All notable changes to this project will be documented in this file.

## [2.0.4-Alpha] - 2021-03-24
- Upgraded new styling from Open Concept SA
- Fix [#296](https://bitbucket.org/cessda/cessda.cvs.two/issues/296/new-agency-vocabulary-term-addition) New agency vocabulary term addition erratic
- Fix [#294](https://bitbucket.org/cessda/cessda.cvs.two/issues/294/export-download-table-does-not-update) Export/download table does not update
- Add more testing on the rest APIs  [#283](https://bitbucket.org/cessda/cessda.cvs.two/issues/283/improve-code-quality-phase-2-80-test)

## [2.0.3-Alpha] - 2021-02-17

### Added

- Add standard CESSDA header to the source-code [#289](https://bitbucket.org/cessda/cessda.cvs.two/issues/289/ensure-source-code-files-contain-standard)
- Improve Api-usage, About and User-guide pages editing functionality which derived from one component
- Improve Quill Rich-TextArea by including table and image plug-ins and fixing missing style 
- Further improving code quality by reuse components [#283](https://bitbucket.org/cessda/cessda.cvs.two/issues/283/improve-code-quality-phase-2-80-test)
- Completely removed unused V1 API [#287](https://bitbucket.org/cessda/cessda.cvs.two/issues/287/upgrade-api-calls-from-v1-to-v2-within-the)


## [2.0.2-Alpha] - 2020-12-15

### Added

- Improve code quality by improving testing coverage, remove duplications and fix code smells [#254](https://bitbucket.org/cessda/cessda.cvs.two/issues/254/improve-cvs-2-code-quality) based on [SonarCube](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
- Fix [#280](https://bitbucket.org/cessda/cessda.cvs.two/issues/280/usage-information-to-be-copied-from-sl-to): Usage information to be copied from SL to TLs
- Fix [#281](https://bitbucket.org/cessda/cessda.cvs.two/issues/281/unable-to-visit-previous-version): Unable to visit previous CV versions
- Fix [#279](https://bitbucket.org/cessda/cessda.cvs.two/issues/279/usage-information-not-shown-in-home): Related to issue in the generation of JSON files for published CVs



## [2.0.1-Alpha] - 2020-12-9

### Added

- Fix and finalize the /v2 REST APIs [#251](https://bitbucket.org/cessda/cessda.cvs.two/issues/251/api-requirements-read-only-mode-api)
    - Add search CV Codes REST API that produces JSON-LD based on Skosmos [#178](https://bitbucket.org/cessda/cessda.cvs.two/issues/178/dataverse-having-problems-consuming-api)
    - Add search Vocabularies & Codes that produces JSON & JSON-LD formats
    - Add export Vocabulary that produces HTML, DOCX, PDF, JSON & JSON-LD formats
- Add application maintenance page, which includes functions to generating JSON for published CVs, indexing for Agency, Vocabularies, Statistics and checking for incorrect CVs.[#272](https://bitbucket.org/cessda/cessda.cvs.two/issues/272/create-a-maintenance-page)
- Provide a configurable URI for Vocabulary versions and Codes. [#256](https://bitbucket.org/cessda/cessda.cvs.two/issues/256/cvs-urls-api-issue), [#271](https://bitbucket.org/cessda/cessda.cvs.two/issues/271/make-agency-uri-configurable-and-link)
- Improve code quality and fix bugs and vulnerabilities [#254](https://bitbucket.org/cessda/cessda.cvs.two/issues/254/improve-cvs-2-code-quality) based on [SonarCube](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs) 
- Fix user agency role issue [#269](https://bitbucket.org/cessda/cessda.cvs.two/issues/269/editing-cv-notes-not-working-for)
- Add Matomo tracking [#266](https://bitbucket.org/cessda/cessda.cvs.two/issues/266/add-matomo-tracking-code)
- Other issues fixing [#277](https://bitbucket.org/cessda/cessda.cvs.two/issues/277/inccorrect-missing-syling-between-ngx), and [#261](https://bitbucket.org/cessda/cessda.cvs.two/issues/261/incorrect-tool-name-in-tab)


## [2.0.0-Alpha] - 2020-11-02

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
