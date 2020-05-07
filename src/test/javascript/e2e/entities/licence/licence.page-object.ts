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
