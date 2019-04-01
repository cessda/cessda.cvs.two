Log in (top right hand corner). All actions that you can perform are down the left hand side of the tool.

‘CV actions’ allow you to make changes to the vocabulary as a whole e.g. vocabulary name or description.
‘Code actions’ are made on individual items (codes) that make up a CV.

Choose ‘Add new CV’.



A new draft with a version number 1.0 DRAFT is created. You can edit this draft using the editing buttons.

There are several mandatory fields to fill in marked with a red asterisk.



Choose the Agency from the drop-down list - the most commonly used ones are CESSDA or DDI. What agency you can choose depends on  your rights and role. If the Agency is not available you will need to request this from the Content Administrator.

Choose the source language; for both CESSDA and DDI CVs this is English. Other languages are in the drop-down list in preparation for future use. The tool may also be used for in-house thesauri.

Add the Short Name (=code) for the CV using the rules specified in the table. This is a machine-actionable field. Important: read the code naming rules carefully first and take good care to get the CV-level code right from the beginning. As this is the ID for the vocabulary, once you have saved it, it is no longer possible to edit it.

If there is a mistake in the CV Short Name code, first of all produce and save an export/download of the vocabulary in its current state, with all language versions, for reference. Next, either 1) drop version (if unpublished), and/or 2) withdraw the CV and 3) create a new one with the corrected Short Name. If the CV had already been published, take care to enter the same version number and version information as in the download.

Option 2) will also withdraw the other language versions. Both for SL and TL, existing information can possibly be copied from the system. This is probably not the case in Phase 1, so it is best not to make mistakes in Short Names or be prepared to do a lot of manual copying.

Rules for creating code terms for SL vocabularies
(adapted from original rules created by Joachim Wackerow)
Code terms within CESSDA Vocabulary Service are the CV short name and the Code value.

Code terms should be self-describing and are based on the human-readable term in question.

A code term is a single English word or a concatenation of several English words:
Each word starts with an upper case character.
Only alphanumeric characters are allowed.
No slashes, replace with ‘Or’, example: EventOrProcess.
No abbreviations are allowed. Only a few exceptions are allowed in the case of acknowledged abbreviation code lists like the two-letter code for the US.
·        
Code terms should be not too long in respect to readability. There is no hard rule for that. It is reasonable not to use more than 5 words and 50 characters.

The code is just a code. It should have a meaning for the purpose that developers can deal with them. Search interfaces should search on the list of captions and/or definitions i.e. the human-readable part.

Hierarchical CVs
One code term exists for each level. The upper level codes get repeated. The concatenation of the codes of all levels are used as an allowed value in DDI. The separator between the levels is a dot. Examples:
Longitudinal.Panel.Continuous
SelfAdministeredQuestionnaire.WebBased

Note: In CESSDA Vocabulary Service, the hierarchy in the Code value is introduced by the system. The user only enters the code value for the individual item itself (e.g. Continuous). When the item is dragged to be a child of another item, the system adds the hierarchy to the code value (Longitudinal.Panel.Continuous).



When adding a code in the tool spaces will automatically be removed.
Next, add a title for the CV.
In CESSDA CVs the title (DDI CV Long Name) is known as the ‘CV name’  and the code (DDI Short Name) as the ‘CV short name’.
Add a definition describing the vocabulary. Save.
Note: You will not be able to save if the CV already exists i.e. the codes are identical.
You now have several actions you can do.

You can make changes to the vocabulary level fields you have already added using the ‘Edit SL en’ button.
You can cancel at any time and no changes will be made. Remember to save if you have made changes.


You can choose to delete your whole draft CV at any status before publication. Click on Drop version and the system will delete the CV altogether. Be careful with Drop Version action, since it will erase the whole CV as this is the first-ever version of it. There is a confirmation pop-up asking whether you are sure you want to carry out this action.


‘Add code’ allows you to add codes for the individual items within the CV. System adds the new code to the end of the vocabulary.
Fill in the ‘Code value’ field using the rules from above, add the descriptive term and definition. Save.
CVs can be hierarchical. Add top-level terms using the ‘Add code’. A narrower term can be added by clicking on the broader term you want to add a child to and then clicking Add child. This will add the item as a child of the currently highlighted code. Note that only the value of the code for the individual term is filled in the code pop-up, the hierarchy in the code (i.e. broader term code) is added by the system.
You can also use dragging to introduce hierarchy. If the item you want to put as a narrower term has already been entered into the CV:
Click on the item and on ‘Enable order code’ (if not enabled already). Drag the item to the term you want it to be a narrower term of. The system asks you whether you want the item to be a sibling or a child. Choose ‘As a child’. The system has now automatically changes the item to have appropriate hierarchy by adding a BT value of the code to the value.
You can also change the place of an item by dragging and choosing it to be a sibling (next or previous), if you want it to be at the same level as the item you have dragged it to.
When all codes are in the correct order you can disable this feature with the ’Disable order code’ button.

Codes can only be ordered within the SL - this is not an option for translations.

All edit buttons for a code appear when you click on the code row. Editing term-level values of the code is possible and does not cause such problems as need to amend the CV-level Short Name.


Add the rest of the items to the CV.

If entering codes from an already existing vocabulary, you can enter the codes by using the ‘Import codes from CSV’ functionality.

Codes need to be in Excel format, first row needs to be empty, and then code values, the descriptive terms and definitions are all in their own columns in this order. Workflow:
Save the Excel in cvs (comma delimited) format.
Check that the they are portrayed like in the example below separated with commas, and definition between quotation marks (“). If there are quotation marks within the definition, change these to ‘.
Family,Family,"Two or more people related by blood..."
Family.HouseholdFamily,Family: Household family,"A more specific term..."
Household,Household,"A person or a group of persons..."
Browse and select the csv file
You will ge a table displaying the codes to be added. Now either click on ‘Import codes’ or cancel.


Once all the codes have been added, ordered and any changes have been made the CV is ready to be reviewed by others.



The screen will show you all the changes that have been made to the CV since the previous version. Change the status to Initial Review.

Changes can be made to all parts of the CV.

There is an internal discussion area, not visible to the unregistered users, where reviewers can share comments on terms.

Once all changes have been agreed between the reviewers and the CV finalised, change the status to Final Review.



At this stage, only the Admin_SL can edit the SL vocabulary. Final changes are made to prepare the CV for publishing including proofing all text, adding usage for vocabularies and checking the licence is correct. Double-check that the version number is correct. If this is a new vocabulary never published before, the version is 1.0. If this is a vocabulary already published somewhere else but new to the tool, it should have its current version number, whatever that is.

You can save your changes before publishing, but when everything is ready, click on Publish.


Click on publish SL button  and publish.

Publishing allows this version of the CV to be viewed by all. Now translations can be added to this published CV, or a new version of the published CV can be created.


See pages xx-xxx for information about Usage and Copyright and License tabs.
