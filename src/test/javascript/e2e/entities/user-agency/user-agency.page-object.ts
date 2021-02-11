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

export class UserAgencyComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-user-agency div table .btn-danger'));
  title = element.all(by.css('jhi-user-agency div h2#page-heading span')).first();
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

export class UserAgencyUpdatePage {
  pageTitle = element(by.id('jhi-user-agency-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  agencyRoleSelect = element(by.id('field_agencyRole'));
  languageSelect = element(by.id('field_language'));

  agencySelect = element(by.id('field_agency'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setAgencyRoleSelect(agencyRole: string): Promise<void> {
    await this.agencyRoleSelect.sendKeys(agencyRole);
  }

  async getAgencyRoleSelect(): Promise<string> {
    return await this.agencyRoleSelect.element(by.css('option:checked')).getText();
  }

  async agencyRoleSelectLastOption(): Promise<void> {
    await this.agencyRoleSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async setLanguageSelect(language: string): Promise<void> {
    await this.languageSelect.sendKeys(language);
  }

  async getLanguageSelect(): Promise<string> {
    return await this.languageSelect.element(by.css('option:checked')).getText();
  }

  async languageSelectLastOption(): Promise<void> {
    await this.languageSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async agencySelectLastOption(): Promise<void> {
    await this.agencySelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async agencySelectOption(option: string): Promise<void> {
    await this.agencySelect.sendKeys(option);
  }

  getAgencySelect(): ElementFinder {
    return this.agencySelect;
  }

  async getAgencySelectedOption(): Promise<string> {
    return await this.agencySelect.element(by.css('option:checked')).getText();
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

export class UserAgencyDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-userAgency-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-userAgency'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
