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

export class ResolverComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-resolver div table .btn-danger'));
  title = element.all(by.css('jhi-resolver div h2#page-heading span')).first();
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

export class ResolverUpdatePage {
  pageTitle = element(by.id('jhi-resolver-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  resourceIdInput = element(by.id('field_resourceId'));
  resourceTypeSelect = element(by.id('field_resourceType'));
  resourceUrlInput = element(by.id('field_resourceUrl'));
  resolverTypeSelect = element(by.id('field_resolverType'));
  resolverURIInput = element(by.id('field_resolverURI'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setResourceIdInput(resourceId: string): Promise<void> {
    await this.resourceIdInput.sendKeys(resourceId);
  }

  async getResourceIdInput(): Promise<string> {
    return await this.resourceIdInput.getAttribute('value');
  }

  async setResourceTypeSelect(resourceType: string): Promise<void> {
    await this.resourceTypeSelect.sendKeys(resourceType);
  }

  async getResourceTypeSelect(): Promise<string> {
    return await this.resourceTypeSelect.element(by.css('option:checked')).getText();
  }

  async resourceTypeSelectLastOption(): Promise<void> {
    await this.resourceTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setResourceUrlInput(resourceUrl: string): Promise<void> {
    await this.resourceUrlInput.sendKeys(resourceUrl);
  }

  async getResourceUrlInput(): Promise<string> {
    return await this.resourceUrlInput.getAttribute('value');
  }

  async setResolverTypeSelect(resolverType: string): Promise<void> {
    await this.resolverTypeSelect.sendKeys(resolverType);
  }

  async getResolverTypeSelect(): Promise<string> {
    return await this.resolverTypeSelect.element(by.css('option:checked')).getText();
  }

  async resolverTypeSelectLastOption(): Promise<void> {
    await this.resolverTypeSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setResolverURIInput(resolverURI: string): Promise<void> {
    await this.resolverURIInput.sendKeys(resolverURI);
  }

  async getResolverURIInput(): Promise<string> {
    return await this.resolverURIInput.getAttribute('value');
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

export class ResolverDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-resolver-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-resolver'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
