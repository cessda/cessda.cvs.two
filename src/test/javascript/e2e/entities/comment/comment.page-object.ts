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

export class CommentComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-comment div table .btn-danger'));
  title = element.all(by.css('jhi-comment div h2#page-heading span')).first();
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

export class CommentUpdatePage {
  pageTitle = element(by.id('jhi-comment-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  infoInput = element(by.id('field_info'));
  contentInput = element(by.id('field_content'));
  userIdInput = element(by.id('field_userId'));
  dateTimeInput = element(by.id('field_dateTime'));

  versionSelect = element(by.id('field_version'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setInfoInput(info: string): Promise<void> {
    await this.infoInput.sendKeys(info);
  }

  async getInfoInput(): Promise<string> {
    return await this.infoInput.getAttribute('value');
  }

  async setContentInput(content: string): Promise<void> {
    await this.contentInput.sendKeys(content);
  }

  async getContentInput(): Promise<string> {
    return await this.contentInput.getAttribute('value');
  }

  async setUserIdInput(userId: string): Promise<void> {
    await this.userIdInput.sendKeys(userId);
  }

  async getUserIdInput(): Promise<string> {
    return await this.userIdInput.getAttribute('value');
  }

  async setDateTimeInput(dateTime: string): Promise<void> {
    await this.dateTimeInput.sendKeys(dateTime);
  }

  async getDateTimeInput(): Promise<string> {
    return await this.dateTimeInput.getAttribute('value');
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

export class CommentDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-comment-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-comment'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
