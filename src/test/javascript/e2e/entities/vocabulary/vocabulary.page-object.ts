/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { element, by, ElementFinder } from 'protractor';

export class VocabularyComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-vocabulary div table .btn-danger'));
  title = element.all(by.css('jhi-vocabulary div h2#page-heading span')).first();
  noResult = element(by.id('no-result'));
  entities = element(by.id('entities'));

  async clickOnCreateButton(): Promise<void> {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(): Promise<void> {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons(): Promise<number> {
    return this.deleteButtons.count();
  }

  async getTitle(): Promise<string> {
    return this.title.getAttribute('jhiTranslate');
  }
}

export class VocabularyUpdatePage {
  pageTitle = element(by.id('jhi-vocabulary-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  statusInput = element(by.id('field_status'));
  uriInput = element(by.id('field_uri'));
  notationInput = element(by.id('field_notation'));
  versionNumberInput = element(by.id('field_versionNumber'));
  initialPublicationInput = element(by.id('field_initialPublication'));
  previousPublicationInput = element(by.id('field_previousPublication'));
  archivedInput = element(by.id('field_archived'));
  withdrawnInput = element(by.id('field_withdrawn'));
  discoverableInput = element(by.id('field_discoverable'));
  sourceLanguageInput = element(by.id('field_sourceLanguage'));
  agencyIdInput = element(by.id('field_agencyId'));
  agencyNameInput = element(by.id('field_agencyName'));
  agencyLogoInput = element(by.id('field_agencyLogo'));
  publicationDateInput = element(by.id('field_publicationDate'));
  lastModifiedInput = element(by.id('field_lastModified'));
  notesInput = element(by.id('field_notes'));
  versionSqInput = element(by.id('field_versionSq'));
  titleSqInput = element(by.id('field_titleSq'));
  definitionSqInput = element(by.id('field_definitionSq'));
  versionBsInput = element(by.id('field_versionBs'));
  titleBsInput = element(by.id('field_titleBs'));
  definitionBsInput = element(by.id('field_definitionBs'));
  versionBgInput = element(by.id('field_versionBg'));
  titleBgInput = element(by.id('field_titleBg'));
  definitionBgInput = element(by.id('field_definitionBg'));
  versionHrInput = element(by.id('field_versionHr'));
  titleHrInput = element(by.id('field_titleHr'));
  definitionHrInput = element(by.id('field_definitionHr'));
  versionCsInput = element(by.id('field_versionCs'));
  titleCsInput = element(by.id('field_titleCs'));
  definitionCsInput = element(by.id('field_definitionCs'));
  versionDaInput = element(by.id('field_versionDa'));
  titleDaInput = element(by.id('field_titleDa'));
  definitionDaInput = element(by.id('field_definitionDa'));
  versionNlInput = element(by.id('field_versionNl'));
  titleNlInput = element(by.id('field_titleNl'));
  definitionNlInput = element(by.id('field_definitionNl'));
  versionEnInput = element(by.id('field_versionEn'));
  titleEnInput = element(by.id('field_titleEn'));
  definitionEnInput = element(by.id('field_definitionEn'));
  versionEtInput = element(by.id('field_versionEt'));
  titleEtInput = element(by.id('field_titleEt'));
  definitionEtInput = element(by.id('field_definitionEt'));
  versionFiInput = element(by.id('field_versionFi'));
  titleFiInput = element(by.id('field_titleFi'));
  definitionFiInput = element(by.id('field_definitionFi'));
  versionFrInput = element(by.id('field_versionFr'));
  titleFrInput = element(by.id('field_titleFr'));
  definitionFrInput = element(by.id('field_definitionFr'));
  versionDeInput = element(by.id('field_versionDe'));
  titleDeInput = element(by.id('field_titleDe'));
  definitionDeInput = element(by.id('field_definitionDe'));
  versionElInput = element(by.id('field_versionEl'));
  titleElInput = element(by.id('field_titleEl'));
  definitionElInput = element(by.id('field_definitionEl'));
  versionHuInput = element(by.id('field_versionHu'));
  titleHuInput = element(by.id('field_titleHu'));
  definitionHuInput = element(by.id('field_definitionHu'));
  versionItInput = element(by.id('field_versionIt'));
  titleItInput = element(by.id('field_titleIt'));
  definitionItInput = element(by.id('field_definitionIt'));
  versionJaInput = element(by.id('field_versionJa'));
  titleJaInput = element(by.id('field_titleJa'));
  definitionJaInput = element(by.id('field_definitionJa'));
  versionLtInput = element(by.id('field_versionLt'));
  titleLtInput = element(by.id('field_titleLt'));
  definitionLtInput = element(by.id('field_definitionLt'));
  versionMkInput = element(by.id('field_versionMk'));
  titleMkInput = element(by.id('field_titleMk'));
  definitionMkInput = element(by.id('field_definitionMk'));
  versionNoInput = element(by.id('field_versionNo'));
  titleNoInput = element(by.id('field_titleNo'));
  definitionNoInput = element(by.id('field_definitionNo'));
  versionPlInput = element(by.id('field_versionPl'));
  titlePlInput = element(by.id('field_titlePl'));
  definitionPlInput = element(by.id('field_definitionPl'));
  versionPtInput = element(by.id('field_versionPt'));
  titlePtInput = element(by.id('field_titlePt'));
  definitionPtInput = element(by.id('field_definitionPt'));
  versionRoInput = element(by.id('field_versionRo'));
  titleRoInput = element(by.id('field_titleRo'));
  definitionRoInput = element(by.id('field_definitionRo'));
  versionRuInput = element(by.id('field_versionRu'));
  titleRuInput = element(by.id('field_titleRu'));
  definitionRuInput = element(by.id('field_definitionRu'));
  versionSrInput = element(by.id('field_versionSr'));
  titleSrInput = element(by.id('field_titleSr'));
  definitionSrInput = element(by.id('field_definitionSr'));
  versionSkInput = element(by.id('field_versionSk'));
  titleSkInput = element(by.id('field_titleSk'));
  definitionSkInput = element(by.id('field_definitionSk'));
  versionSlInput = element(by.id('field_versionSl'));
  titleSlInput = element(by.id('field_titleSl'));
  definitionSlInput = element(by.id('field_definitionSl'));
  versionEsInput = element(by.id('field_versionEs'));
  titleEsInput = element(by.id('field_titleEs'));
  definitionEsInput = element(by.id('field_definitionEs'));
  versionSvInput = element(by.id('field_versionSv'));
  titleSvInput = element(by.id('field_titleSv'));
  definitionSvInput = element(by.id('field_definitionSv'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setStatusInput(status: string): Promise<void> {
    await this.statusInput.sendKeys(status);
  }

  async getStatusInput(): Promise<string> {
    return await this.statusInput.getAttribute('value');
  }

  async setUriInput(uri: string): Promise<void> {
    await this.uriInput.sendKeys(uri);
  }

  async getUriInput(): Promise<string> {
    return await this.uriInput.getAttribute('value');
  }

  async setNotationInput(notation: string): Promise<void> {
    await this.notationInput.sendKeys(notation);
  }

  async getNotationInput(): Promise<string> {
    return await this.notationInput.getAttribute('value');
  }

  async setVersionNumberInput(versionNumber: string): Promise<void> {
    await this.versionNumberInput.sendKeys(versionNumber);
  }

  async getVersionNumberInput(): Promise<string> {
    return await this.versionNumberInput.getAttribute('value');
  }

  async setInitialPublicationInput(initialPublication: string): Promise<void> {
    await this.initialPublicationInput.sendKeys(initialPublication);
  }

  async getInitialPublicationInput(): Promise<string> {
    return await this.initialPublicationInput.getAttribute('value');
  }

  async setPreviousPublicationInput(previousPublication: string): Promise<void> {
    await this.previousPublicationInput.sendKeys(previousPublication);
  }

  async getPreviousPublicationInput(): Promise<string> {
    return await this.previousPublicationInput.getAttribute('value');
  }

  getArchivedInput(): ElementFinder {
    return this.archivedInput;
  }

  getWithdrawnInput(): ElementFinder {
    return this.withdrawnInput;
  }

  getDiscoverableInput(): ElementFinder {
    return this.discoverableInput;
  }

  async setSourceLanguageInput(sourceLanguage: string): Promise<void> {
    await this.sourceLanguageInput.sendKeys(sourceLanguage);
  }

  async getSourceLanguageInput(): Promise<string> {
    return await this.sourceLanguageInput.getAttribute('value');
  }

  async setAgencyIdInput(agencyId: string): Promise<void> {
    await this.agencyIdInput.sendKeys(agencyId);
  }

  async getAgencyIdInput(): Promise<string> {
    return await this.agencyIdInput.getAttribute('value');
  }

  async setAgencyNameInput(agencyName: string): Promise<void> {
    await this.agencyNameInput.sendKeys(agencyName);
  }

  async getAgencyNameInput(): Promise<string> {
    return await this.agencyNameInput.getAttribute('value');
  }

  async setAgencyLogoInput(agencyLogo: string): Promise<void> {
    await this.agencyLogoInput.sendKeys(agencyLogo);
  }

  async getAgencyLogoInput(): Promise<string> {
    return await this.agencyLogoInput.getAttribute('value');
  }

  async setPublicationDateInput(publicationDate: string): Promise<void> {
    await this.publicationDateInput.sendKeys(publicationDate);
  }

  async getPublicationDateInput(): Promise<string> {
    return await this.publicationDateInput.getAttribute('value');
  }

  async setLastModifiedInput(lastModified: string): Promise<void> {
    await this.lastModifiedInput.sendKeys(lastModified);
  }

  async getLastModifiedInput(): Promise<string> {
    return await this.lastModifiedInput.getAttribute('value');
  }

  async setNotesInput(notes: string): Promise<void> {
    await this.notesInput.sendKeys(notes);
  }

  async getNotesInput(): Promise<string> {
    return await this.notesInput.getAttribute('value');
  }

  async setVersionSqInput(versionSq: string): Promise<void> {
    await this.versionSqInput.sendKeys(versionSq);
  }

  async getVersionSqInput(): Promise<string> {
    return await this.versionSqInput.getAttribute('value');
  }

  async setTitleSqInput(titleSq: string): Promise<void> {
    await this.titleSqInput.sendKeys(titleSq);
  }

  async getTitleSqInput(): Promise<string> {
    return await this.titleSqInput.getAttribute('value');
  }

  async setDefinitionSqInput(definitionSq: string): Promise<void> {
    await this.definitionSqInput.sendKeys(definitionSq);
  }

  async getDefinitionSqInput(): Promise<string> {
    return await this.definitionSqInput.getAttribute('value');
  }

  async setVersionBsInput(versionBs: string): Promise<void> {
    await this.versionBsInput.sendKeys(versionBs);
  }

  async getVersionBsInput(): Promise<string> {
    return await this.versionBsInput.getAttribute('value');
  }

  async setTitleBsInput(titleBs: string): Promise<void> {
    await this.titleBsInput.sendKeys(titleBs);
  }

  async getTitleBsInput(): Promise<string> {
    return await this.titleBsInput.getAttribute('value');
  }

  async setDefinitionBsInput(definitionBs: string): Promise<void> {
    await this.definitionBsInput.sendKeys(definitionBs);
  }

  async getDefinitionBsInput(): Promise<string> {
    return await this.definitionBsInput.getAttribute('value');
  }

  async setVersionBgInput(versionBg: string): Promise<void> {
    await this.versionBgInput.sendKeys(versionBg);
  }

  async getVersionBgInput(): Promise<string> {
    return await this.versionBgInput.getAttribute('value');
  }

  async setTitleBgInput(titleBg: string): Promise<void> {
    await this.titleBgInput.sendKeys(titleBg);
  }

  async getTitleBgInput(): Promise<string> {
    return await this.titleBgInput.getAttribute('value');
  }

  async setDefinitionBgInput(definitionBg: string): Promise<void> {
    await this.definitionBgInput.sendKeys(definitionBg);
  }

  async getDefinitionBgInput(): Promise<string> {
    return await this.definitionBgInput.getAttribute('value');
  }

  async setVersionHrInput(versionHr: string): Promise<void> {
    await this.versionHrInput.sendKeys(versionHr);
  }

  async getVersionHrInput(): Promise<string> {
    return await this.versionHrInput.getAttribute('value');
  }

  async setTitleHrInput(titleHr: string): Promise<void> {
    await this.titleHrInput.sendKeys(titleHr);
  }

  async getTitleHrInput(): Promise<string> {
    return await this.titleHrInput.getAttribute('value');
  }

  async setDefinitionHrInput(definitionHr: string): Promise<void> {
    await this.definitionHrInput.sendKeys(definitionHr);
  }

  async getDefinitionHrInput(): Promise<string> {
    return await this.definitionHrInput.getAttribute('value');
  }

  async setVersionCsInput(versionCs: string): Promise<void> {
    await this.versionCsInput.sendKeys(versionCs);
  }

  async getVersionCsInput(): Promise<string> {
    return await this.versionCsInput.getAttribute('value');
  }

  async setTitleCsInput(titleCs: string): Promise<void> {
    await this.titleCsInput.sendKeys(titleCs);
  }

  async getTitleCsInput(): Promise<string> {
    return await this.titleCsInput.getAttribute('value');
  }

  async setDefinitionCsInput(definitionCs: string): Promise<void> {
    await this.definitionCsInput.sendKeys(definitionCs);
  }

  async getDefinitionCsInput(): Promise<string> {
    return await this.definitionCsInput.getAttribute('value');
  }

  async setVersionDaInput(versionDa: string): Promise<void> {
    await this.versionDaInput.sendKeys(versionDa);
  }

  async getVersionDaInput(): Promise<string> {
    return await this.versionDaInput.getAttribute('value');
  }

  async setTitleDaInput(titleDa: string): Promise<void> {
    await this.titleDaInput.sendKeys(titleDa);
  }

  async getTitleDaInput(): Promise<string> {
    return await this.titleDaInput.getAttribute('value');
  }

  async setDefinitionDaInput(definitionDa: string): Promise<void> {
    await this.definitionDaInput.sendKeys(definitionDa);
  }

  async getDefinitionDaInput(): Promise<string> {
    return await this.definitionDaInput.getAttribute('value');
  }

  async setVersionNlInput(versionNl: string): Promise<void> {
    await this.versionNlInput.sendKeys(versionNl);
  }

  async getVersionNlInput(): Promise<string> {
    return await this.versionNlInput.getAttribute('value');
  }

  async setTitleNlInput(titleNl: string): Promise<void> {
    await this.titleNlInput.sendKeys(titleNl);
  }

  async getTitleNlInput(): Promise<string> {
    return await this.titleNlInput.getAttribute('value');
  }

  async setDefinitionNlInput(definitionNl: string): Promise<void> {
    await this.definitionNlInput.sendKeys(definitionNl);
  }

  async getDefinitionNlInput(): Promise<string> {
    return await this.definitionNlInput.getAttribute('value');
  }

  async setVersionEnInput(versionEn: string): Promise<void> {
    await this.versionEnInput.sendKeys(versionEn);
  }

  async getVersionEnInput(): Promise<string> {
    return await this.versionEnInput.getAttribute('value');
  }

  async setTitleEnInput(titleEn: string): Promise<void> {
    await this.titleEnInput.sendKeys(titleEn);
  }

  async getTitleEnInput(): Promise<string> {
    return await this.titleEnInput.getAttribute('value');
  }

  async setDefinitionEnInput(definitionEn: string): Promise<void> {
    await this.definitionEnInput.sendKeys(definitionEn);
  }

  async getDefinitionEnInput(): Promise<string> {
    return await this.definitionEnInput.getAttribute('value');
  }

  async setVersionEtInput(versionEt: string): Promise<void> {
    await this.versionEtInput.sendKeys(versionEt);
  }

  async getVersionEtInput(): Promise<string> {
    return await this.versionEtInput.getAttribute('value');
  }

  async setTitleEtInput(titleEt: string): Promise<void> {
    await this.titleEtInput.sendKeys(titleEt);
  }

  async getTitleEtInput(): Promise<string> {
    return await this.titleEtInput.getAttribute('value');
  }

  async setDefinitionEtInput(definitionEt: string): Promise<void> {
    await this.definitionEtInput.sendKeys(definitionEt);
  }

  async getDefinitionEtInput(): Promise<string> {
    return await this.definitionEtInput.getAttribute('value');
  }

  async setVersionFiInput(versionFi: string): Promise<void> {
    await this.versionFiInput.sendKeys(versionFi);
  }

  async getVersionFiInput(): Promise<string> {
    return await this.versionFiInput.getAttribute('value');
  }

  async setTitleFiInput(titleFi: string): Promise<void> {
    await this.titleFiInput.sendKeys(titleFi);
  }

  async getTitleFiInput(): Promise<string> {
    return await this.titleFiInput.getAttribute('value');
  }

  async setDefinitionFiInput(definitionFi: string): Promise<void> {
    await this.definitionFiInput.sendKeys(definitionFi);
  }

  async getDefinitionFiInput(): Promise<string> {
    return await this.definitionFiInput.getAttribute('value');
  }

  async setVersionFrInput(versionFr: string): Promise<void> {
    await this.versionFrInput.sendKeys(versionFr);
  }

  async getVersionFrInput(): Promise<string> {
    return await this.versionFrInput.getAttribute('value');
  }

  async setTitleFrInput(titleFr: string): Promise<void> {
    await this.titleFrInput.sendKeys(titleFr);
  }

  async getTitleFrInput(): Promise<string> {
    return await this.titleFrInput.getAttribute('value');
  }

  async setDefinitionFrInput(definitionFr: string): Promise<void> {
    await this.definitionFrInput.sendKeys(definitionFr);
  }

  async getDefinitionFrInput(): Promise<string> {
    return await this.definitionFrInput.getAttribute('value');
  }

  async setVersionDeInput(versionDe: string): Promise<void> {
    await this.versionDeInput.sendKeys(versionDe);
  }

  async getVersionDeInput(): Promise<string> {
    return await this.versionDeInput.getAttribute('value');
  }

  async setTitleDeInput(titleDe: string): Promise<void> {
    await this.titleDeInput.sendKeys(titleDe);
  }

  async getTitleDeInput(): Promise<string> {
    return await this.titleDeInput.getAttribute('value');
  }

  async setDefinitionDeInput(definitionDe: string): Promise<void> {
    await this.definitionDeInput.sendKeys(definitionDe);
  }

  async getDefinitionDeInput(): Promise<string> {
    return await this.definitionDeInput.getAttribute('value');
  }

  async setVersionElInput(versionEl: string): Promise<void> {
    await this.versionElInput.sendKeys(versionEl);
  }

  async getVersionElInput(): Promise<string> {
    return await this.versionElInput.getAttribute('value');
  }

  async setTitleElInput(titleEl: string): Promise<void> {
    await this.titleElInput.sendKeys(titleEl);
  }

  async getTitleElInput(): Promise<string> {
    return await this.titleElInput.getAttribute('value');
  }

  async setDefinitionElInput(definitionEl: string): Promise<void> {
    await this.definitionElInput.sendKeys(definitionEl);
  }

  async getDefinitionElInput(): Promise<string> {
    return await this.definitionElInput.getAttribute('value');
  }

  async setVersionHuInput(versionHu: string): Promise<void> {
    await this.versionHuInput.sendKeys(versionHu);
  }

  async getVersionHuInput(): Promise<string> {
    return await this.versionHuInput.getAttribute('value');
  }

  async setTitleHuInput(titleHu: string): Promise<void> {
    await this.titleHuInput.sendKeys(titleHu);
  }

  async getTitleHuInput(): Promise<string> {
    return await this.titleHuInput.getAttribute('value');
  }

  async setDefinitionHuInput(definitionHu: string): Promise<void> {
    await this.definitionHuInput.sendKeys(definitionHu);
  }

  async getDefinitionHuInput(): Promise<string> {
    return await this.definitionHuInput.getAttribute('value');
  }

  async setVersionItInput(versionIt: string): Promise<void> {
    await this.versionItInput.sendKeys(versionIt);
  }

  async getVersionItInput(): Promise<string> {
    return await this.versionItInput.getAttribute('value');
  }

  async setTitleItInput(titleIt: string): Promise<void> {
    await this.titleItInput.sendKeys(titleIt);
  }

  async getTitleItInput(): Promise<string> {
    return await this.titleItInput.getAttribute('value');
  }

  async setDefinitionItInput(definitionIt: string): Promise<void> {
    await this.definitionItInput.sendKeys(definitionIt);
  }

  async getDefinitionItInput(): Promise<string> {
    return await this.definitionItInput.getAttribute('value');
  }

  async setVersionJaInput(versionJa: string): Promise<void> {
    await this.versionJaInput.sendKeys(versionJa);
  }

  async getVersionJaInput(): Promise<string> {
    return await this.versionJaInput.getAttribute('value');
  }

  async setTitleJaInput(titleJa: string): Promise<void> {
    await this.titleJaInput.sendKeys(titleJa);
  }

  async getTitleJaInput(): Promise<string> {
    return await this.titleJaInput.getAttribute('value');
  }

  async setDefinitionJaInput(definitionJa: string): Promise<void> {
    await this.definitionJaInput.sendKeys(definitionJa);
  }

  async getDefinitionJaInput(): Promise<string> {
    return await this.definitionJaInput.getAttribute('value');
  }

  async setVersionLtInput(versionLt: string): Promise<void> {
    await this.versionLtInput.sendKeys(versionLt);
  }

  async getVersionLtInput(): Promise<string> {
    return await this.versionLtInput.getAttribute('value');
  }

  async setTitleLtInput(titleLt: string): Promise<void> {
    await this.titleLtInput.sendKeys(titleLt);
  }

  async getTitleLtInput(): Promise<string> {
    return await this.titleLtInput.getAttribute('value');
  }

  async setDefinitionLtInput(definitionLt: string): Promise<void> {
    await this.definitionLtInput.sendKeys(definitionLt);
  }

  async getDefinitionLtInput(): Promise<string> {
    return await this.definitionLtInput.getAttribute('value');
  }

  async setVersionMkInput(versionMk: string): Promise<void> {
    await this.versionMkInput.sendKeys(versionMk);
  }

  async getVersionMkInput(): Promise<string> {
    return await this.versionMkInput.getAttribute('value');
  }

  async setTitleMkInput(titleMk: string): Promise<void> {
    await this.titleMkInput.sendKeys(titleMk);
  }

  async getTitleMkInput(): Promise<string> {
    return await this.titleMkInput.getAttribute('value');
  }

  async setDefinitionMkInput(definitionMk: string): Promise<void> {
    await this.definitionMkInput.sendKeys(definitionMk);
  }

  async getDefinitionMkInput(): Promise<string> {
    return await this.definitionMkInput.getAttribute('value');
  }

  async setVersionNoInput(versionNo: string): Promise<void> {
    await this.versionNoInput.sendKeys(versionNo);
  }

  async getVersionNoInput(): Promise<string> {
    return await this.versionNoInput.getAttribute('value');
  }

  async setTitleNoInput(titleNo: string): Promise<void> {
    await this.titleNoInput.sendKeys(titleNo);
  }

  async getTitleNoInput(): Promise<string> {
    return await this.titleNoInput.getAttribute('value');
  }

  async setDefinitionNoInput(definitionNo: string): Promise<void> {
    await this.definitionNoInput.sendKeys(definitionNo);
  }

  async getDefinitionNoInput(): Promise<string> {
    return await this.definitionNoInput.getAttribute('value');
  }

  async setVersionPlInput(versionPl: string): Promise<void> {
    await this.versionPlInput.sendKeys(versionPl);
  }

  async getVersionPlInput(): Promise<string> {
    return await this.versionPlInput.getAttribute('value');
  }

  async setTitlePlInput(titlePl: string): Promise<void> {
    await this.titlePlInput.sendKeys(titlePl);
  }

  async getTitlePlInput(): Promise<string> {
    return await this.titlePlInput.getAttribute('value');
  }

  async setDefinitionPlInput(definitionPl: string): Promise<void> {
    await this.definitionPlInput.sendKeys(definitionPl);
  }

  async getDefinitionPlInput(): Promise<string> {
    return await this.definitionPlInput.getAttribute('value');
  }

  async setVersionPtInput(versionPt: string): Promise<void> {
    await this.versionPtInput.sendKeys(versionPt);
  }

  async getVersionPtInput(): Promise<string> {
    return await this.versionPtInput.getAttribute('value');
  }

  async setTitlePtInput(titlePt: string): Promise<void> {
    await this.titlePtInput.sendKeys(titlePt);
  }

  async getTitlePtInput(): Promise<string> {
    return await this.titlePtInput.getAttribute('value');
  }

  async setDefinitionPtInput(definitionPt: string): Promise<void> {
    await this.definitionPtInput.sendKeys(definitionPt);
  }

  async getDefinitionPtInput(): Promise<string> {
    return await this.definitionPtInput.getAttribute('value');
  }

  async setVersionRoInput(versionRo: string): Promise<void> {
    await this.versionRoInput.sendKeys(versionRo);
  }

  async getVersionRoInput(): Promise<string> {
    return await this.versionRoInput.getAttribute('value');
  }

  async setTitleRoInput(titleRo: string): Promise<void> {
    await this.titleRoInput.sendKeys(titleRo);
  }

  async getTitleRoInput(): Promise<string> {
    return await this.titleRoInput.getAttribute('value');
  }

  async setDefinitionRoInput(definitionRo: string): Promise<void> {
    await this.definitionRoInput.sendKeys(definitionRo);
  }

  async getDefinitionRoInput(): Promise<string> {
    return await this.definitionRoInput.getAttribute('value');
  }

  async setVersionRuInput(versionRu: string): Promise<void> {
    await this.versionRuInput.sendKeys(versionRu);
  }

  async getVersionRuInput(): Promise<string> {
    return await this.versionRuInput.getAttribute('value');
  }

  async setTitleRuInput(titleRu: string): Promise<void> {
    await this.titleRuInput.sendKeys(titleRu);
  }

  async getTitleRuInput(): Promise<string> {
    return await this.titleRuInput.getAttribute('value');
  }

  async setDefinitionRuInput(definitionRu: string): Promise<void> {
    await this.definitionRuInput.sendKeys(definitionRu);
  }

  async getDefinitionRuInput(): Promise<string> {
    return await this.definitionRuInput.getAttribute('value');
  }

  async setVersionSrInput(versionSr: string): Promise<void> {
    await this.versionSrInput.sendKeys(versionSr);
  }

  async getVersionSrInput(): Promise<string> {
    return await this.versionSrInput.getAttribute('value');
  }

  async setTitleSrInput(titleSr: string): Promise<void> {
    await this.titleSrInput.sendKeys(titleSr);
  }

  async getTitleSrInput(): Promise<string> {
    return await this.titleSrInput.getAttribute('value');
  }

  async setDefinitionSrInput(definitionSr: string): Promise<void> {
    await this.definitionSrInput.sendKeys(definitionSr);
  }

  async getDefinitionSrInput(): Promise<string> {
    return await this.definitionSrInput.getAttribute('value');
  }

  async setVersionSkInput(versionSk: string): Promise<void> {
    await this.versionSkInput.sendKeys(versionSk);
  }

  async getVersionSkInput(): Promise<string> {
    return await this.versionSkInput.getAttribute('value');
  }

  async setTitleSkInput(titleSk: string): Promise<void> {
    await this.titleSkInput.sendKeys(titleSk);
  }

  async getTitleSkInput(): Promise<string> {
    return await this.titleSkInput.getAttribute('value');
  }

  async setDefinitionSkInput(definitionSk: string): Promise<void> {
    await this.definitionSkInput.sendKeys(definitionSk);
  }

  async getDefinitionSkInput(): Promise<string> {
    return await this.definitionSkInput.getAttribute('value');
  }

  async setVersionSlInput(versionSl: string): Promise<void> {
    await this.versionSlInput.sendKeys(versionSl);
  }

  async getVersionSlInput(): Promise<string> {
    return await this.versionSlInput.getAttribute('value');
  }

  async setTitleSlInput(titleSl: string): Promise<void> {
    await this.titleSlInput.sendKeys(titleSl);
  }

  async getTitleSlInput(): Promise<string> {
    return await this.titleSlInput.getAttribute('value');
  }

  async setDefinitionSlInput(definitionSl: string): Promise<void> {
    await this.definitionSlInput.sendKeys(definitionSl);
  }

  async getDefinitionSlInput(): Promise<string> {
    return await this.definitionSlInput.getAttribute('value');
  }

  async setVersionEsInput(versionEs: string): Promise<void> {
    await this.versionEsInput.sendKeys(versionEs);
  }

  async getVersionEsInput(): Promise<string> {
    return await this.versionEsInput.getAttribute('value');
  }

  async setTitleEsInput(titleEs: string): Promise<void> {
    await this.titleEsInput.sendKeys(titleEs);
  }

  async getTitleEsInput(): Promise<string> {
    return await this.titleEsInput.getAttribute('value');
  }

  async setDefinitionEsInput(definitionEs: string): Promise<void> {
    await this.definitionEsInput.sendKeys(definitionEs);
  }

  async getDefinitionEsInput(): Promise<string> {
    return await this.definitionEsInput.getAttribute('value');
  }

  async setVersionSvInput(versionSv: string): Promise<void> {
    await this.versionSvInput.sendKeys(versionSv);
  }

  async getVersionSvInput(): Promise<string> {
    return await this.versionSvInput.getAttribute('value');
  }

  async setTitleSvInput(titleSv: string): Promise<void> {
    await this.titleSvInput.sendKeys(titleSv);
  }

  async getTitleSvInput(): Promise<string> {
    return await this.titleSvInput.getAttribute('value');
  }

  async setDefinitionSvInput(definitionSv: string): Promise<void> {
    await this.definitionSvInput.sendKeys(definitionSv);
  }

  async getDefinitionSvInput(): Promise<string> {
    return await this.definitionSvInput.getAttribute('value');
  }

  async save(): Promise<void> {
    await this.saveButton.click();
  }

  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class VocabularyDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-vocabulary-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-vocabulary'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
