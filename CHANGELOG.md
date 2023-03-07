# Changelog

All notable changes to the CESSDA Vocabulary Service source code are documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

_For each release, use the following sub-sections:_

- _Added (for new features)_
- _Changed (for changes in existing functionality)_
- _Deprecated (for soon-to-be removed features)_
- _Removed (for now removed features)_
- _Fixed (for any bug fixes)_
- _Security (in case of vulnerabilities)_

## [3.0.1] - 2023-03-07

- [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.xxxx.svg)](https://doi.org/10.5281/zenodo.xxxx)

### Additions

- [#482](https://github.com/cessda/cessda.cvs.two/issues/482) Add the Maven Release Plugin to the POM

### Fixes

- [#484](https://github.com/cessda/cessda.cvs.two/issues/484) Fix CVs showing the wrong "Available from" link and canonical URI
- [#487](https://github.com/cessda/cessda.cvs.two/issues/487) Fix CV export not working for older versions of CVs

### Removals

- [#479]((https://github.com/cessda/cessda.cvs.two/issues/479) Remove the database migration button from the UI

### Security

- Applied security fixes suggested by Dependabot and `npm audit`

## [3.0.0] - 2023-01-31

- [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.7473085.svg)](https://doi.org/10.5281/zenodo.7473085)

### Additions

- [#440](https://github.com/cessda/cessda.cvs.two/issues/440) Added code deprecation button for SL Admin role
- [#99](https://github.com/cessda/cessda.cvs.two/issues/99) Content Editor Guide is now available online to authorised users
- [#430](https://github.com/cessda/cessda.cvs.two/issues/430) When new codes are added to SL, they are copied to TL drafts.
  When codes are deprecated in SL, they are removed from TL drafts
- [#409](https://github.com/cessda/cessda.cvs.two/issues/409) Added link to CESSDA Data Catalogue in About page
- [#393](https://github.com/cessda/cessda.cvs.two/issues/393) Pop-up warning is displayed when a code value is changed
- [#385](https://github.com/cessda/cessda.cvs.two/issues/385) Added workflow for deprecating codes

### Changes

- [#465](https://github.com/cessda/cessda.cvs.two/issues/465) Updated copyright statement for 2023
- [#463](https://github.com/cessda/cessda.cvs.two/issues/465) Updated About page to reflect the User facing changes in this version
- [#461](https://github.com/cessda/cessda.cvs.two/issues/461) Updated User guide to reflect the User facing changes in this version
- [#441](https://github.com/cessda/cessda.cvs.two/issues/441) Modified text in 'Create new SL version' pop-up
- [#437](https://github.com/cessda/cessda.cvs.two/issues/437) Changed label of Publish CV button
- [#436](https://github.com/cessda/cessda.cvs.two/issues/436) 'Contributor SL' and 'Contributor TL' roles have been removed
- [#423](https://github.com/cessda/cessda.cvs.two/issues/423) Deprecated codes info is now visible in TL pop-up
- [#422](https://github.com/cessda/cessda.cvs.two/issues/422) Deprecated terms in search results are indicated by the postfix '(DEPRECATED TERM)'
- [#421](https://github.com/cessda/cessda.cvs.two/issues/421) Deprecated terms are marked as such in CV exports (all formats)
- [#420](https://github.com/cessda/cessda.cvs.two/issues/420) Code definitions are now visible in the details table, prior to publication
- [#419](https://github.com/cessda/cessda.cvs.two/issues/419) The pop-up shown when transitioning a CV from draft to review status
  no longer contains the comparison table and change logs
- [#418](https://github.com/cessda/cessda.cvs.two/issues/418) Modified text in deprecation pop-up
- [#398](https://github.com/cessda/cessda.cvs.two/issues/398) CVs now use 3 digit versioning
- [#394](https://github.com/cessda/cessda.cvs.two/issues/394) CV editing workflow now contains READY TO BE TRANSLATED and PUBLISHED statuses
- [#386](https://github.com/cessda/cessda.cvs.two/issues/386) Updated Content Editors' guide to reflect the User facing changes in this version

### Fixes

- [#468](https://github.com/cessda/cessda.cvs.two/issues/468) Unable to create new SL version
- [#462](https://github.com/cessda/cessda.cvs.two/issues/462) Unable to add codes to existing vocabulary
- [#446](https://github.com/cessda/cessda.cvs.two/issues/446) Some CVs had spurious 'newer version' information
- [#457](https://github.com/cessda/cessda.cvs.two/issues/457) Missing action buttons have been restored for various Admin roles
- [#424](https://github.com/cessda/cessda.cvs.two/issues/424) Corrected alphabetical ordering for vocabulary search results list
- [#329](https://github.com/cessda/cessda.cvs.two/issues/329) Missing space in citation - corrected

### Security

- None

## [2.2.0] - 2022-06-30

- [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.6598225.svg)](https://doi.org/10.5281/zenodo.6598225)

### Additions

- [#390](https://github.com/cessda/cessda.cvs.two/issues/390) Add Lithuanian and Estonian to target languages
- [#381](https://github.com/cessda/cessda.cvs.two/issues/381) Add Japanese to PDF and SKOS exports
- [#380](https://github.com/cessda/cessda.cvs.two/issues/380) Add Japanese to target languages
- [#306](https://github.com/cessda/cessda.cvs.two/issues/306) Add Matomo Cloud tracking code
- [#274](https://github.com/cessda/cessda.cvs.two/issues/274) Structured logs are ingested into EFK system
- [#263](https://github.com/cessda/cessda.cvs.two/issues/263) Add Swagger validator badge to API documentation page

### Changes

- [#406](https://github.com/cessda/cessda.cvs.two/issues/406) Updated versions of images referenced in Docker Compose files
- [#392](https://github.com/cessda/cessda.cvs.two/issues/392) Documentation updates for current release
- [#389](https://github.com/cessda/cessda.cvs.two/issues/389) Update usage text for Rest APIs
- [#388](https://github.com/cessda/cessda.cvs.two/issues/388) Change label and target of REST API link
- [#360](https://github.com/cessda/cessda.cvs.two/issues/360) Remove term ‘CVS’ from User-facing materials

### Fixes

- [#396](https://github.com/cessda/cessda.cvs.two/issues/396) Usage information was not saved
- [#391](https://github.com/cessda/cessda.cvs.two/issues/391) SL_Admin was not able to publish CVs
- [#375](https://github.com/cessda/cessda.cvs.two/issues/375) Language tags were not in alphabetical order
- [#374](https://github.com/cessda/cessda.cvs.two/issues/374) Password reset email used wrong field to salute user
- [#363](https://github.com/cessda/cessda.cvs.two/issues/363) CESSDA Topic Classification CV opened slowly in Editor
- [#329](https://github.com/cessda/cessda.cvs.two/issues/329) Add missing space to citation text
- [#316](https://github.com/cessda/cessda.cvs.two/issues/316) Unable to read CVS file with multiline content inside quotes for import

### Security

- None

## [2.1.0] - 2022-03-03

- [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.6092399.svg)](https://doi.org/10.5281/zenodo.6092399)

### Additions

- [#369](https://github.com/cessda/cessda.cvs.two/issues/369) UI: Add Dutch to the drop-down list in search
- [#335](https://github.com/cessda/cessda.cvs.two/issues/369) Notification for 'no change type' entered
- [#312](https://github.com/cessda/cessda.cvs.two/issues/369) Reinstate 'Send feedback' tab

### Changes

- [#352](https://github.com/cessda/cessda.cvs.two/issues/352) UI look and feel
- [#328](https://github.com/cessda/cessda.cvs.two/issues/328) Remove default change type from Edit TL pop-up
- [#327](https://github.com/cessda/cessda.cvs.two/issues/327) UI mixes two languages for TL admins
- [#326](https://github.com/cessda/cessda.cvs.two/issues/326) User Guide tabs
- [#305](https://github.com/cessda/cessda.cvs.two/issues/305) CESSDA MO address change in footer
- [#298](https://github.com/cessda/cessda.cvs.two/issues/298) Improve documentation and service description

### Removals

- None

### Fixes

- [#373](https://github.com/cessda/cessda.cvs.two/issues/373) Change log is emptied in publication pop-up
- [#362](https://github.com/cessda/cessda.cvs.two/issues/362) Export/download in draft status inactive again
- [#350](https://github.com/cessda/cessda.cvs.two/issues/350) CESSDA Persistent Identifier Types FI button
- [#349](https://github.com/cessda/cessda.cvs.two/issues/249) Adding new codes does not keep order
- [#348](https://github.com/cessda/cessda.cvs.two/issues/348) Problems with saving in Agency
- [#344](https://github.com/cessda/cessda.cvs.two/issues/344) Password reset issue
- [#342](https://github.com/cessda/cessda.cvs.two/issues/342) Account activation
- [#330](https://github.com/cessda/cessda.cvs.two/issues/330) Export/download of CVs in draft status not functioning
- [#322](https://github.com/cessda/cessda.cvs.two/issues/322) Saving not working in publishing pop-up
- [#319](https://github.com/cessda/cessda.cvs.two/issues/319) Current text disappears when editing Usage
- [#310](https://github.com/cessda/cessda.cvs.two/issues/310) Editing the usage field of vocabularies no longer works as intended
- [#309](https://github.com/cessda/cessda.cvs.two/issues/309) Agency logo not updating for existing vocabularies bug
- [#308](https://github.com/cessda/cessda.cvs.two/issues/308) Do not store data on local filesystem
- [#290](https://github.com/cessda/cessda.cvs.two/issues/290) No license information for some CESSDA vocabularies

### Security

- None

## [2.0.6-Alpha] - 2021-04-19

- Fix [#294](https://github.com/cessda/cessda.cvs.two/issues/294/export-download-table-does-not-update)
  Export/download table does not update, change cache control to no-store
- Fix [#302](https://github.com/cessda/cessda.cvs.two/issues/302/bug-in-skos-export-script)
  Fix incorrect Thymeleaf template to generate RDF

## [2.0.5-Alpha] - 2021-04-12

- Fix [#300](https://github.com/cessda/cessda.cvs.two/issues/300/fix-incorrect-http-headers-related-to)
  Fix incorrect HTTP headers related to cache-control and content-type for JSON files

## [2.0.4-Alpha] - 2021-03-25

- Upgraded new styling from Open Concept SA
- Fix [#296](https://github.com/cessda/cessda.cvs.two/issues/296/new-agency-vocabulary-term-addition)
  New agency vocabulary term addition erratic
- Add more testing on the rest APIs
  [#283](https://github.com/cessda/cessda.cvs.two/issues/283/improve-code-quality-phase-2-80-test)

## [2.0.3-Alpha] - 2021-02-17

- Add standard CESSDA header to the source-code
  [#289](https://github.com/cessda/cessda.cvs.two/issues/289/ensure-source-code-files-contain-standard)
- Improve Api-usage, About and User-guide pages editing functionality which derived from one component
- Improve Quill Rich-TextArea by including table and image plug-ins and fixing missing style
- Further improving code quality by reuse components [#283](https://github.com/cessda/cessda.cvs.two/issues/283/improve-code-quality-phase-2-80-test)
- Completely removed unused V1 API [#287](https://github.com/cessda/cessda.cvs.two/issues/287/upgrade-api-calls-from-v1-to-v2-within-the)

## [2.0.2-Alpha] - 2020-12-15

- Improve code quality by improving testing coverage, remove duplications and fix code smells
  [#254](https://github.com/cessda/cessda.cvs.two/issues/254/improve-cvs-2-code-quality)
  based on [SonarCube](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
- Fix [#280](https://github.com/cessda/cessda.cvs.two/issues/280/usage-information-to-be-copied-from-sl-to):
  Usage information to be copied from SL to TLs
- Fix [#281](https://github.com/cessda/cessda.cvs.two/issues/281/unable-to-visit-previous-version): Unable to visit previous CV versions
- Fix [#279](https://github.com/cessda/cessda.cvs.two/issues/279/usage-information-not-shown-in-home): Related to issue in the generation of JSON files for published CVs

## [2.0.1-Alpha] - 2020-12-9

- Fix and finalize the /v2 REST APIs [#251](https://github.com/cessda/cessda.cvs.two/issues/251/api-requirements-read-only-mode-api)
- Add search CV Codes REST API that produces JSON-LD based on Skosmos [#178](https://github.com/cessda/cessda.cvs.two/issues/178/dataverse-having-problems-consuming-api)
- Add search Vocabularies & Codes that produces JSON & JSON-LD formats
- Add export Vocabulary that produces HTML, DOCX, PDF, JSON & JSON-LD formats
- Add application maintenance page, which includes functions to generating JSON for published CVs, indexing for Agency, Vocabularies,
  Statistics and checking for incorrect CVs. [#272](https://github.com/cessda/cessda.cvs.two/issues/272/create-a-maintenance-page)
- Provide a configurable URI for Vocabulary versions and Codes. [#256](https://github.com/cessda/cessda.cvs.two/issues/256/cvs-urls-api-issue),
  [#271](https://github.com/cessda/cessda.cvs.two/issues/271/make-agency-uri-configurable-and-link)
- Improve code quality and fix bugs and vulnerabilities [#254](https://github.com/cessda/cessda.cvs.two/issues/254/improve-cvs-2-code-quality)
  based on [SonarCube](https://sonarqube.cessda.eu/dashboard?id=eu.cessda.cvs%3Acvs)
- Fix user agency role issue [#269](https://github.com/cessda/cessda.cvs.two/issues/269/editing-cv-notes-not-working-for)
- Add Matomo tracking [#266](https://github.com/cessda/cessda.cvs.two/issues/266/add-matomo-tracking-code)
- Other issues fixing [#277](https://github.com/cessda/cessda.cvs.two/issues/277/inccorrect-missing-syling-between-ngx),
  and [#261](https://github.com/cessda/cessda.cvs.two/issues/261/incorrect-tool-name-in-tab)

## [2.0.0-Alpha] - 2020-11-02

The first version of CESSDA Vocabulary Service 2, which was re-developed from scratch based on the deprecated version 1 application.
The project uses new architecture Spring-Boot framework for the back-end and Angular for the front-end, instead of Vaadin like in version 1.

- Back-end uses Spring-Boot v 2.2.5.RELEASE.
- Front-end uses Angular v 10.1.5.
- Improve Docker support, compare to version 1.
- Responsive web design
- Improved styling and looks-and-feels
- JSON Web Token (JWT) authentication, a stateless security mechanism. In order to improve scalability.
- Support for 28 Controlled-Vocabularies (CVs) languages (Albanian (sq), Bosnian (bs), Bulgarian (bg),
  Croatian (hr), Czech (cs), Danish (da), Dutch (nl), English (en), Estonian (et), Finnish (fi),
  French (fr), German (de), Greek (el), Hungarian (hu), Italian (it), Japanese (ja), Lithuanian (lt),
  Macedonian (mk), Norwegian (no), Polish (pl), Portuguese (pt), Romanian (ro), Russian (ru), Serbian (sr),
  Slovak (sk), Slovenian (sl), Spanish (es), Swedish (sv))
- Fixed corrupted CVs from the version 1
- Fixed and improved Vocabulary workflow
- Fixed vocabulary cloning and versioning
- Store published-vocabulary in JSON format
- New URLs for both Publication and Editor pages, for better readability
- Bookmarkable URLs for searching and vocabulary
- Vocabulary and Codes searching with Elastic-Search
- New redesign Search UI, search Vocabulary based on specific language
- Search filters (e.g based on Agency) with [ngx-chips](https://www.npmjs.com/package/ngx-chips) component.
- Optimized and fixed searching functionality from version 1
- Compare vocabulary versions with [ngx-text-diff](https://www.npmjs.com/package/ngx-text-diff) component,
  which based on `google diff match patch` library.
- Responsive Tree component adapts screen-size, to show the Vocabulary codes hierarchical structure.
- Vocabulary version comments functionality.
- Rich-Textarea with [ngx-quill](https://www.npmjs.com/package/ngx-quill) component.
- Agency page, with agency detail and statistic.
- Charts library using [ngx-charts](https://swimlane.gitbook.io/ngx-charts/) component.
- Admin page with user-management and licence-management.
- Application technical tools and matrices such as JVM-Metrics, App Health Checks, Configuration, Logs Audit.
- Swagger UI for the APIs.
- New set of /v2 REST APIs for searching and exporting CVs to several formats (PDF, HTML, DOCX)
