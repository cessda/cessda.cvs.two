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

export class ConceptComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-concept div table .btn-danger'));
  title = element.all(by.css('jhi-concept div h2#page-heading span')).first();
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

export class ConceptUpdatePage {
  pageTitle = element(by.id('jhi-concept-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  uriInput = element(by.id('field_uri'));
  notationInput = element(by.id('field_notation'));
  titleInput = element(by.id('field_title'));
  definitionInput = element(by.id('field_definition'));
  previousConceptInput = element(by.id('field_previousConcept'));
  slConceptInput = element(by.id('field_slConcept'));
  parentInput = element(by.id('field_parent'));
  positionInput = element(by.id('field_position'));

  versionSelect = element(by.id('field_version'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
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

  async setPreviousConceptInput(previousConcept: string): Promise<void> {
    await this.previousConceptInput.sendKeys(previousConcept);
  }

  async getPreviousConceptInput(): Promise<string> {
    return await this.previousConceptInput.getAttribute('value');
  }

  async setSlConceptInput(slConcept: string): Promise<void> {
    await this.slConceptInput.sendKeys(slConcept);
  }

  async getSlConceptInput(): Promise<string> {
    return await this.slConceptInput.getAttribute('value');
  }

  async setParentInput(parent: string): Promise<void> {
    await this.parentInput.sendKeys(parent);
  }

  async getParentInput(): Promise<string> {
    return await this.parentInput.getAttribute('value');
  }

  async setPositionInput(position: string): Promise<void> {
    await this.positionInput.sendKeys(position);
  }

  async getPositionInput(): Promise<string> {
    return await this.positionInput.getAttribute('value');
  }

  async versionSelectLastOption(): Promise<void> {
    await this.versionSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async versionSelectOption(option: string): Promise<void> {
    await this.versionSelect.sendKeys(option);
  }

  getVersionSelect(): ElementFinder {
    return this.versionSelect;
  }

  async getVersionSelectedOption(): Promise<string> {
    return await this.versionSelect.element(by.css('option:checked')).getText();
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

export class ConceptDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-concept-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-concept'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
