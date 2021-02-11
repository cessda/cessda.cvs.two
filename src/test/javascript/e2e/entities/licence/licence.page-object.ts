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

export class LicenceComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-licence div table .btn-danger'));
  title = element.all(by.css('jhi-licence div h2#page-heading span')).first();
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

export class LicenceUpdatePage {
  pageTitle = element(by.id('jhi-licence-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  nameInput = element(by.id('field_name'));
  linkInput = element(by.id('field_link'));
  logoLinkInput = element(by.id('field_logoLink'));
  abbrInput = element(by.id('field_abbr'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setNameInput(name: string): Promise<void> {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput(): Promise<string> {
    return await this.nameInput.getAttribute('value');
  }

  async setLinkInput(link: string): Promise<void> {
    await this.linkInput.sendKeys(link);
  }

  async getLinkInput(): Promise<string> {
    return await this.linkInput.getAttribute('value');
  }

  async setLogoLinkInput(logoLink: string): Promise<void> {
    await this.logoLinkInput.sendKeys(logoLink);
  }

  async getLogoLinkInput(): Promise<string> {
    return await this.logoLinkInput.getAttribute('value');
  }

  async setAbbrInput(abbr: string): Promise<void> {
    await this.abbrInput.sendKeys(abbr);
  }

  async getAbbrInput(): Promise<string> {
    return await this.abbrInput.getAttribute('value');
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

export class LicenceDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-licence-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-licence'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
