/*
 * Copyright © 2017-2021 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import { element, by, ElementFinder } from 'protractor';

export class VersionComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-version div table .btn-danger'));
  title = element.all(by.css('jhi-version div h2#page-heading span')).first();
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

export class VersionUpdatePage {
  pageTitle = element(by.id('jhi-version-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  statusInput = element(by.id('field_status'));
  itemTypeInput = element(by.id('field_itemType'));
  languageInput = element(by.id('field_language'));
  publicationDateInput = element(by.id('field_publicationDate'));
  lastModifiedInput = element(by.id('field_lastModified'));
  numberInput = element(by.id('field_number'));
  uriInput = element(by.id('field_uri'));
  canonicalUriInput = element(by.id('field_canonicalUri'));
  uriSlInput = element(by.id('field_uriSl'));
  notationInput = element(by.id('field_notation'));
  titleInput = element(by.id('field_title'));
  definitionInput = element(by.id('field_definition'));
  previousVersionInput = element(by.id('field_previousVersion'));
  initialVersionInput = element(by.id('field_initialVersion'));
  creatorInput = element(by.id('field_creator'));
  publisherInput = element(by.id('field_publisher'));
  notesInput = element(by.id('field_notes'));
  versionNotesInput = element(by.id('field_versionNotes'));
  versionChangesInput = element(by.id('field_versionChanges'));
  discussionNotesInput = element(by.id('field_discussionNotes'));
  licenseInput = element(by.id('field_license'));
  licenseIdInput = element(by.id('field_licenseId'));
  citationInput = element(by.id('field_citation'));
  ddiUsageInput = element(by.id('field_ddiUsage'));
  translateAgencyInput = element(by.id('field_translateAgency'));
  translateAgencyLinkInput = element(by.id('field_translateAgencyLink'));

  vocabularySelect = element(by.id('field_vocabulary'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setStatusInput(status: string): Promise<void> {
    await this.statusInput.sendKeys(status);
  }

  async getStatusInput(): Promise<string> {
    return await this.statusInput.getAttribute('value');
  }

  async setItemTypeInput(itemType: string): Promise<void> {
    await this.itemTypeInput.sendKeys(itemType);
  }

  async getItemTypeInput(): Promise<string> {
    return await this.itemTypeInput.getAttribute('value');
  }

  async setLanguageInput(language: string): Promise<void> {
    await this.languageInput.sendKeys(language);
  }

  async getLanguageInput(): Promise<string> {
    return await this.languageInput.getAttribute('value');
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

  async setNumberInput(number: string): Promise<void> {
    await this.numberInput.sendKeys(number);
  }

  async getNumberInput(): Promise<string> {
    return await this.numberInput.getAttribute('value');
  }

  async setUriInput(uri: string): Promise<void> {
    await this.uriInput.sendKeys(uri);
  }

  async getUriInput(): Promise<string> {
    return await this.uriInput.getAttribute('value');
  }

  async setCanonicalUriInput(canonicalUri: string): Promise<void> {
    await this.canonicalUriInput.sendKeys(canonicalUri);
  }

  async getCanonicalUriInput(): Promise<string> {
    return await this.canonicalUriInput.getAttribute('value');
  }

  async setUriSlInput(uriSl: string): Promise<void> {
    await this.uriSlInput.sendKeys(uriSl);
  }

  async getUriSlInput(): Promise<string> {
    return await this.uriSlInput.getAttribute('value');
  }

  async setNotationInput(notation: string): Promise<void> {
    await this.notationInput.sendKeys(notation);
  }

  async getNotationInput(): Promise<string> {
    return await this.notationInput.getAttribute('value');
  }

  async setTitleInput(title: string): Promise<void> {
    await this.titleInput.sendKeys(title);
  }

  async getTitleInput(): Promise<string> {
    return await this.titleInput.getAttribute('value');
  }

  async setDefinitionInput(definition: string): Promise<void> {
    await this.definitionInput.sendKeys(definition);
  }

  async getDefinitionInput(): Promise<string> {
    return await this.definitionInput.getAttribute('value');
  }

  async setPreviousVersionInput(previousVersion: string): Promise<void> {
    await this.previousVersionInput.sendKeys(previousVersion);
  }

  async getPreviousVersionInput(): Promise<string> {
    return await this.previousVersionInput.getAttribute('value');
  }

  async setInitialVersionInput(initialVersion: string): Promise<void> {
    await this.initialVersionInput.sendKeys(initialVersion);
  }

  async getInitialVersionInput(): Promise<string> {
    return await this.initialVersionInput.getAttribute('value');
  }

  async setCreatorInput(creator: string): Promise<void> {
    await this.creatorInput.sendKeys(creator);
  }

  async getCreatorInput(): Promise<string> {
    return await this.creatorInput.getAttribute('value');
  }

  async setPublisherInput(publisher: string): Promise<void> {
    await this.publisherInput.sendKeys(publisher);
  }

  async getPublisherInput(): Promise<string> {
    return await this.publisherInput.getAttribute('value');
  }

  async setNotesInput(notes: string): Promise<void> {
    await this.notesInput.sendKeys(notes);
  }

  async getNotesInput(): Promise<string> {
    return await this.notesInput.getAttribute('value');
  }

  async setVersionNotesInput(versionNotes: string): Promise<void> {
    await this.versionNotesInput.sendKeys(versionNotes);
  }

  async getVersionNotesInput(): Promise<string> {
    return await this.versionNotesInput.getAttribute('value');
  }

  async setVersionChangesInput(versionChanges: string): Promise<void> {
    await this.versionChangesInput.sendKeys(versionChanges);
  }

  async getVersionChangesInput(): Promise<string> {
    return await this.versionChangesInput.getAttribute('value');
  }

  async setDiscussionNotesInput(discussionNotes: string): Promise<void> {
    await this.discussionNotesInput.sendKeys(discussionNotes);
  }

  async getDiscussionNotesInput(): Promise<string> {
    return await this.discussionNotesInput.getAttribute('value');
  }

  async setLicenseInput(license: string): Promise<void> {
    await this.licenseInput.sendKeys(license);
  }

  async getLicenseInput(): Promise<string> {
    return await this.licenseInput.getAttribute('value');
  }

  async setLicenseIdInput(licenseId: string): Promise<void> {
    await this.licenseIdInput.sendKeys(licenseId);
  }

  async getLicenseIdInput(): Promise<string> {
    return await this.licenseIdInput.getAttribute('value');
  }

  async setCitationInput(citation: string): Promise<void> {
    await this.citationInput.sendKeys(citation);
  }

  async getCitationInput(): Promise<string> {
    return await this.citationInput.getAttribute('value');
  }

  async setDdiUsageInput(ddiUsage: string): Promise<void> {
    await this.ddiUsageInput.sendKeys(ddiUsage);
  }

  async getDdiUsageInput(): Promise<string> {
    return await this.ddiUsageInput.getAttribute('value');
  }

  async setTranslateAgencyInput(translateAgency: string): Promise<void> {
    await this.translateAgencyInput.sendKeys(translateAgency);
  }

  async getTranslateAgencyInput(): Promise<string> {
    return await this.translateAgencyInput.getAttribute('value');
  }

  async setTranslateAgencyLinkInput(translateAgencyLink: string): Promise<void> {
    await this.translateAgencyLinkInput.sendKeys(translateAgencyLink);
  }

  async getTranslateAgencyLinkInput(): Promise<string> {
    return await this.translateAgencyLinkInput.getAttribute('value');
  }

  async vocabularySelectLastOption(): Promise<void> {
    await this.vocabularySelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async vocabularySelectOption(option: string): Promise<void> {
    await this.vocabularySelect.sendKeys(option);
  }

  getVocabularySelect(): ElementFinder {
    return this.vocabularySelect;
  }

  async getVocabularySelectedOption(): Promise<string> {
    return await this.vocabularySelect.element(by.css('option:checked')).getText();
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

export class VersionDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-version-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-version'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
