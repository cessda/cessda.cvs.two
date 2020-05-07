import { element, by, ElementFinder } from 'protractor';

export class AgencyComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-agency div table .btn-danger'));
  title = element.all(by.css('jhi-agency div h2#page-heading span')).first();
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

export class AgencyUpdatePage {
  pageTitle = element(by.id('jhi-agency-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  nameInput = element(by.id('field_name'));
  linkInput = element(by.id('field_link'));
  descriptionInput = element(by.id('field_description'));
  logopathInput = element(by.id('field_logopath'));
  licenseInput = element(by.id('field_license'));
  licenseIdInput = element(by.id('field_licenseId'));
  uriInput = element(by.id('field_uri'));
  canonicalUriInput = element(by.id('field_canonicalUri'));

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

  async setDescriptionInput(description: string): Promise<void> {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput(): Promise<string> {
    return await this.descriptionInput.getAttribute('value');
  }

  async setLogopathInput(logopath: string): Promise<void> {
    await this.logopathInput.sendKeys(logopath);
  }

  async getLogopathInput(): Promise<string> {
    return await this.logopathInput.getAttribute('value');
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

export class AgencyDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-agency-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-agency'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getAttribute('jhiTranslate');
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
