import { element, by, ElementFinder } from 'protractor';

export class MetadataFieldComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-metadata-field div table .btn-danger'));
  title = element.all(by.css('jhi-metadata-field div h2#page-heading span')).first();
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

export class MetadataFieldUpdatePage {
  pageTitle = element(by.id('jhi-metadata-field-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  metadataKeyInput = element(by.id('field_metadataKey'));
  descriptionInput = element(by.id('field_description'));
  objectTypeSelect = element(by.id('field_objectType'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getAttribute('jhiTranslate');
  }

  async setMetadataKeyInput(metadataKey: string): Promise<void> {
    await this.metadataKeyInput.sendKeys(metadataKey);
  }

  async getMetadataKeyInput(): Promise<string> {
    return await this.metadataKeyInput.getAttribute('value');
  }

  async setDescriptionInput(description: string): Promise<void> {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput(): Promise<string> {
    return await this.descriptionInput.getAttribute('value');
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

export class MetadataFieldDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-metadataField-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-metadataField'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
