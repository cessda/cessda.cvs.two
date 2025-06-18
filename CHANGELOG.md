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

## [3.5.0] - 2025-06-17

### Added

- [#835](https://github.com/cessda/cessda.cvs.two/issues/835) Added the Translation button to already published vocabularies
- [#1058](https://github.com/cessda/cessda.cvs.two/issues/1058) Added validation so that users cannot set a version number for a TL that is lower than the SL version number
- [PR-1026](https://github.com/cessda/cessda.cvs.two/pull/1026) Added cache period and use last modified settings, set last modified in the Docker image to allow HTTP caching to work correctly

### Changed

- [PR-1028](https://github.com/cessda/cessda.cvs.two/pull/1028) Fixed problems found by code inspection
- [#332](https://github.com/cessda/cessda.cvs.two/issues/332) Cleaned up JSON-LD related methods, don't set JSON-LD properties which are `null`
- [#1022](https://github.com/cessda/cessda.cvs.two/issues/1022) Use enums in place of strings where appropriate in the backend

### Fixed

- [#1030](https://github.com/cessda/cessda.cvs.two/issues/1030) Fixed PDF generation sometimes hanging by replacing iTextPDF html2pdf with Flying Saucer
- [#1063](https://github.com/cessda/cessda.cvs.two/issues/1063) Fixed performing an unnecessary search refresh on keyup events by storing the last search query in the session storage
- [#1063](https://github.com/cessda/cessda.cvs.two/issues/1063) Only perform a search request if text was changed or the enter key was pressed

## [3.4.2] - 2025-05-06

### Fixed

- [#1045](https://github.com/cessda/cessda.cvs.two/issues/1045) Fixed missing Swagger API documentation by importing the Swagger profile into the base CVS configuration
- [#1038](https://github.com/cessda/cessda.cvs.two/issues/1038) Fixed stretched agency images in the agency overview page

## [3.4.1] - 2025-04-09

### Added

- [PR-1034](https://github.com/cessda/cessda.cvs.two/pull/1034) Added the agency name to the alt text of an agency logo

### Fixed

- [#1036](https://github.com/cessda/cessda.cvs.two/issues/1036) Fixed user account creation failing due to missing `langKey` value
- [#1039](https://github.com/cessda/cessda.cvs.two/issues/1039) Fixed CORS requests failing due to the use of `jhipster.cors.allowed-origins` rather than `jhipster.cors.allowed-origin-patterns`

## [3.4.0] - 2025-03-18

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.14849410.svg)](https://doi.org/10.5281/zenodo.14849410)

### Important Dependency Updates

- [PR-864](https://github.com/cessda/cessda.cvs.two/pull/864), [PR-915](https://github.com/cessda/cessda.cvs.two/pull/915), [PR-965](https://github.com/cessda/cessda.cvs.two/pull/965) Updated Angular from version 12 to version 15
- [PR-910](https://github.com/cessda/cessda.cvs.two/pull/910) Updated Spring Boot to version 2.4.13

### Added

- [#926](https://github.com/cessda/cessda.cvs.two/issues/926), [PR-958](https://github.com/cessda/cessda.cvs.two/pull/958) Generate and store temporary instances of exported resources on the file system
- [PR-905](https://github.com/cessda/cessda.cvs.two/pull/905) Added tests for SecurityUtils methods
- [PR-954](https://github.com/cessda/cessda.cvs.two/pull/954) Refactored the static file path mechanism to allow the directory to serve and upload static resources from to be configured
- [PR-964](https://github.com/cessda/cessda.cvs.two/pull/964) Implemented `Cache-Control` headers for non-secured API endpoints

### Changed

- [#794](https://github.com/cessda/cessda.cvs.two/issues/794) Collapse the 'extra data' field in audit display by default
- [#885](https://github.com/cessda/cessda.cvs.two/issues/885) Updated the SQAaaS quality check badge
- [PR-774](https://github.com/cessda/cessda.cvs.two/pull/774) Improved type safety throughout the web application
- [PR-802](https://github.com/cessda/cessda.cvs.two/pull/802) Removed redundant catch blocks that only logged an error message
- [PR-846](https://github.com/cessda/cessda.cvs.two/pull/846) Converted some requests that return HTTP 500 status codes to return more semantically correct HTTP 400 codes
- [PR-949](https://github.com/cessda/cessda.cvs.two/pull/949) Migrated `UntypedFormBuilder` instances to typed `FormBuilder` instances
- [PR-990](https://github.com/cessda/cessda.cvs.two/pull/990) Enabled ESLint reports in Jenkins

### Fixed

- [#805](https://github.com/cessda/cessda.cvs.two/issues/805) Fixed unmapped Mapstruct properties warnings being emitted when compiling CVS
- [#902](https://github.com/cessda/cessda.cvs.two/issues/902) Fixed the audit logs repeating every 30 entries
- [#969](https://github.com/cessda/cessda.cvs.two/issues/969) Fixed the search field resetting and blocking entries when searching from a vocabulary
- [#983](https://github.com/cessda/cessda.cvs.two/issues/983) Fixed showing "No vocabularies found" whilst the search was initializing
- [#1006](https://github.com/cessda/cessda.cvs.two/issues/1006) Fixed styling issues caused by changes in Bootstrap 5
- [PR-804](https://github.com/cessda/cessda.cvs.two/pull/804) Fixed various Java compiler warnings
- [PR-890](https://github.com/cessda/cessda.cvs.two/pull/890) Fixed XSS warnings raised by CodeQL
- [PR-943](https://github.com/cessda/cessda.cvs.two/pull/943) Fixed tab selections breaking because of the removal of the `ngbRadioGroup` attribute from Angular Bootstrap
- [PR-950](https://github.com/cessda/cessda.cvs.two/pull/950) Fixed XSS vulnerabilities reported by CodeQL by not using the provided file names to derive any part of the uploaded file names
- [PR-954](https://github.com/cessda/cessda.cvs.two/pull/954) Fixed most warnings logged by `html2pdf`
- [PR-954](https://github.com/cessda/cessda.cvs.two/pull/954) Fixed licence images not being converted correctly
- [PR-977](https://github.com/cessda/cessda.cvs.two/pull/977) Fixed agency and licence image uploads not working after PR-954
- [PR-980](https://github.com/cessda/cessda.cvs.two/pull/980) Fixed rendering of close buttons in modal dialogs
- [PR-990](https://github.com/cessda/cessda.cvs.two/pull/990) Fixed errors and warnings reported by ESLint

## [3.3.1] - 2023-11-21

### Fixed

- [#669](https://github.com/cessda/cessda.cvs.two/issues/669) Fixed not being able to access the second search page when navigating back to the search page
- [#786](https://github.com/cessda/cessda.cvs.two/issues/786) Fixed the search language filter having no effect if applied after navigating back to the search page
- [#809](https://github.com/cessda/cessda.cvs.two/issues/809) Fixed the agency filter having no effect if applied after navigating back to the search page
- [#814](https://github.com/cessda/cessda.cvs.two/issues/814) Fixed Quill editing fields being blank
- [#819](https://github.com/cessda/cessda.cvs.two/issues/819) Fixed pagination issues in the user management component
- [PR-816](https://github.com/cessda/cessda.cvs.two/pull/816) Fixed possible infinite recursion on the search page where the search would continuously re-request the same query
- [PR-816](https://github.com/cessda/cessda.cvs.two/pull/816) Fixed searches not starting from page 1, resulting in search results being inaccessible

## [3.3.0] - 2023-11-07

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10009383.svg)](https://doi.org/10.5281/zenodo.10009383)

### Dependency updates

- [PR-601](https://github.com/cessda/cessda.cvs.two/pull/601) Update Node.JS to 16.20.0
- [PR-641](https://github.com/cessda/cessda.cvs.two/pull/641) Update Java to version 11
- [PR-731](https://github.com/cessda/cessda.cvs.two/pull/731) and [PR-738](https://github.com/cessda/cessda.cvs.two/pull/738) Update Angular from version 10 to version 12

### Added

- [#219](https://github.com/cessda/cessda.cvs.two/issues/219) Added the UTF-8 BOM to the CSV export
- [#378](https://github.com/cessda/cessda.cvs.two/issues/378) Implemented System Audit for Registered Users´ Events
- [#455](https://github.com/cessda/cessda.cvs.two/issues/455) Implemented Concept-level URIs
- [#490](https://github.com/cessda/cessda.cvs.two/issues/490) Added tests to check that Vocabulary exports are non-null
- [#726](https://github.com/cessda/cessda.cvs.two/issues/726) Updated SQAaaS badge after the latest assessment

### Changed

- [PR-661](https://github.com/cessda/cessda.cvs.two/pull/661) Implement code fixes recommended by static analysis tools such as SonarQube
- [PR-757](https://github.com/cessda/cessda.cvs.two/pull/757) Build the Angular application using the Angular CLI
- [PR-769](https://github.com/cessda/cessda.cvs.two/pull/769) Enabled the `strictTemplates` Angular compiler option
- [PR-772](https://github.com/cessda/cessda.cvs.two/pull/772) Build the Angular component in a Node.js Docker container
- [PR-806](https://github.com/cessda/cessda.cvs.two/pull/806) Explicitly load fonts into the PDF font provider
- [PR-807](https://github.com/cessda/cessda.cvs.two/pull/807) Use the V8 coverage provider to gather code coverage metrics in Jest tests

### Fixed

- [PR-663](https://github.com/cessda/cessda.cvs.two/pull/663) Corrected the delete bundle CV functionality
- [PR-780](https://github.com/cessda/cessda.cvs.two/pull/780) Fixed various code smells detected by SonarQube
- [#438](https://github.com/cessda/cessda.cvs.two/issues/438) Fixed PDF and HTML exports in general
- [#667](https://github.com/cessda/cessda.cvs.two/issues/667) Fixed version number in SKOS export
- [#669](https://github.com/cessda/cessda.cvs.two/issues/669) Fixed not being able to access subsequent pages after the first page
- [#701](https://github.com/cessda/cessda.cvs.two/issues/701) Fixed exporting the licence name rather than the licence URL in the RDF export
- [#702](https://github.com/cessda/cessda.cvs.two/issues/702) Fixed the presence of NUL characters/empty bytes in various export types (SKOS, PDF, HTML)
- [#703](https://github.com/cessda/cessda.cvs.two/issues/703) Fixed returning SKOS RDF with the content type set to `application/json` rather than the correct content type `application/rdf+xml`
- [#797](https://github.com/cessda/cessda.cvs.two/issues/797) Fixed invalid Swagger definitions caused by not having the licence name set

### Security

- [#283](https://github.com/cessda/cessda.cvs.two/issues/283) Fixed security hotspots identified by SonarQube

## [3.2.0] - 2023-05-09

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10009345.svg)](https://doi.org/10.5281/zenodo.10009345)

## Changes

- [#456](https://github.com/cessda/cessda.cvs.two/issues/456) Added "Code definition added" to the Edit SL/TL window
- [#581](https://github.com/cessda/cessda.cvs.two/issues/581) De-emphasise out-of-bundle versions
- [PR-619](https://github.com/cessda/cessda.cvs.two/pull/619) Clean up POM so that Spring Boot is imported correctly

## Fixes

- [#604](https://github.com/cessda/cessda.cvs.two/issues/604) Fix duplicate language entries in SKOS exports by filtering out versions that are not the latest version

## [3.1.0] - 2023-04-04

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.7795784.svg)](https://doi.org/10.5281/zenodo.7795784)

## Changes

- [#459](https://github.com/cessda/cessda.cvs.two/issues/459)/[PR-570](https://github.com/cessda/cessda.cvs.two/pull/570)
  Renamed "License and Citation" to "Licence and citation"
- [#472](https://github.com/cessda/cessda.cvs.two/issues/472)/[PR-583](https://github.com/cessda/cessda.cvs.two/pull/583)
  Correct the rights for different roles
- [PR-572](https://github.com/cessda/cessda.cvs.two/pull/572) Set the root logging level to WARN for the `prod` profile

## Fixes

- [#571](https://github.com/cessda/cessda.cvs.two/issues/571)/[PR-573](https://github.com/cessda/cessda.cvs.two/pull/573)
  Fixed names of SKOS exported file including languages not included in that version
- [PR-569](https://github.com/cessda/cessda.cvs.two/pull/569) Disable logging for `com.itextpdf` for anything below "
  ERROR"
- [PR-572](https://github.com/cessda/cessda.cvs.two/pull/572) Fixed `UserDetails.equals()` always
  throwing `ClassCastException`

## [3.0.1] - 2023-03-07

### Additions

- [#482](https://github.com/cessda/cessda.cvs.two/issues/482) Added the Maven Release Plugin to the POM

### Changes

- [#464](https://github.com/cessda/cessda.cvs.two/issues/464) Database changes for v3.0.0

### Fixes

- [#484](https://github.com/cessda/cessda.cvs.two/issues/484) Fixed CVs showing the wrong "Available from" link and canonical URI
- [#487](https://github.com/cessda/cessda.cvs.two/issues/487) Fixed CV export not working for older versions of CVs
- [#489](https://github.com/cessda/cessda.cvs.two/issues/489) Fixed `null` values being present in the SKOS export
- [#491](https://github.com/cessda/cessda.cvs.two/issues/491) Fixed a discrepancy in the breadcrumb version number

### Removals

- [#479](https://github.com/cessda/cessda.cvs.two/issues/479) Removed the database migration button from the UI

### Security

- [#554](https://github.com/cessda/cessda.cvs.two/issues/554) Applied security fixes suggested by Dependabot and `npm audit`

## [3.0.0] - 2023-01-31

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.7473085.svg)](https://doi.org/10.5281/zenodo.7473085)

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

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.6598225.svg)](https://doi.org/10.5281/zenodo.6598225)

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

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.6092399.svg)](https://doi.org/10.5281/zenodo.6092399)

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

[3.5.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.5.0
[3.4.2]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.4.2
[3.4.1]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.4.1
[3.4.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.4.0
[3.3.1]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.3.1
[3.3.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.3.0
[3.2.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.2.0
[3.1.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.1.0
[3.0.1]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.0.1
[3.0.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/3.0.0
[2.2.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.2.0
[2.1.0]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.1.0
[2.0.6-alpha]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.0.6
[2.0.5-alpha]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.0.5
[2.0.4-alpha]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.0.4
[2.0.3-alpha]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.0.3
[2.0.2-alpha]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.0.2
[2.0.1-alpha]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.0.1
[2.0.0-alpha]: https://github.com/cessda/cessda.cvs.two/releases/tag/v2.0.0
