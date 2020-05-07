import { element, by, ElementFinder } from 'protractor';

export class VocabularyChangeComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-vocabulary-change div table .btn-danger'));
  title = element.all(by.css('jhi-vocabulary-change div h2#page-heading span')).first();
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

export class VocabularyChangeUpdatePage {
  pageTitle = element(by.id('jhi-vocabulary-change-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  vocabularyIdInput = element(by.id('field_vocabularyId'));
  versionIdInput = element(by.id('field_versionId'));
  changeTypeInput = element(by.id('field_changeType'));
  descriptionInput = element(by.id('field_description'));
  userIdInput = element(by.id('field_userId'));
  userNameInput = element(by.id('field_userName'));
  dateInput = element(by.id('field_date'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setVocabularyIdInput(vocabularyId: string): Promise<void> {
    await this.vocabularyIdInput.sendKeys(vocabularyId);
  }

  async getVocabularyIdInput(): Promise<string> {
    return await this.vocabularyIdInput.getAttribute('value');
  }

  async setVersionIdInput(versionId: string): Promise<void> {
    await this.versionIdInput.sendKeys(versionId);
  }

  async getVersionIdInput(): Promise<string> {
    return await this.versionIdInput.getAttribute('value');
  }

  async setChangeTypeInput(changeType: string): Promise<void> {
    await this.changeTypeInput.sendKeys(changeType);
  }

  async getChangeTypeInput(): Promise<string> {
    return await this.changeTypeInput.getAttribute('value');
  }

  async setDescriptionInput(description: string): Promise<void> {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput(): Promise<string> {
    return await this.descriptionInput.getAttribute('value');
  }

  async setUserIdInput(userId: string): Promise<void> {
    await this.userIdInput.sendKeys(userId);
  }

  async getUserIdInput(): Promise<string> {
    return await this.userIdInput.getAttribute('value');
  }

  async setUserNameInput(userName: string): Promise<void> {
    await this.userNameInput.sendKeys(userName);
  }

  async getUserNameInput(): Promise<string> {
    return await this.userNameInput.getAttribute('value');
  }

  async setDateInput(date: string): Promise<void> {
    await this.dateInput.sendKeys(date);
  }

  async getDateInput(): Promise<string> {
    return await this.dateInput.getAttribute('value');
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

export class VocabularyChangeDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-vocabularyChange-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-vocabularyChange'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
