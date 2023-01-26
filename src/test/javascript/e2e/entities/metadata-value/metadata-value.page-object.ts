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

export class MetadataValueComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-metadata-value div table .btn-danger'));
  title = element.all(by.css('jhi-metadata-value div h2#page-heading span')).first();
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

export class MetadataValueUpdatePage {
  pageTitle = element(by.id('jhi-metadata-value-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  valueInput = element(by.id('field_value'));
  objectTypeSelect = element(by.id('field_objectType'));
  objectIdInput = element(by.id('field_objectId'));

  metadataFieldSelect = element(by.id('field_metadataField'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setValueInput(value: string): Promise<void> {
    await this.valueInput.sendKeys(value);
  }

  async getValueInput(): Promise<string> {
    return await this.valueInput.getAttribute('value');
  }

  async setObjectTypeSelect(objectType: string): Promise<void> {
    await this.objectTypeSelect.sendKeys(objectType);
  }

  async getObjectTypeSelect(): Promise<string> {
    return await this.objectTypeSelect.element(by.css('option:checked')).getText();
  }

  async objectTypeSelectLastOption(): Promise<void> {
    await this.objectTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setObjectIdInput(objectId: string): Promise<void> {
    await this.objectIdInput.sendKeys(objectId);
  }

  async getObjectIdInput(): Promise<string> {
    return await this.objectIdInput.getAttribute('value');
  }

  async metadataFieldSelectLastOption(): Promise<void> {
    await this.metadataFieldSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async metadataFieldSelectOption(option: string): Promise<void> {
    await this.metadataFieldSelect.sendKeys(option);
  }

  getMetadataFieldSelect(): ElementFinder {
    return this.metadataFieldSelect;
  }

  async getMetadataFieldSelectedOption(): Promise<string> {
    return await this.metadataFieldSelect.element(by.css('option:checked')).getText();
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

export class MetadataValueDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-metadataValue-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-metadataValue'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
