export interface IConcept {
  id?: number;
  uri?: string;
  notation?: string;
  title?: any;
  definition?: any;
  previousConcept?: number;
  slConcept?: number;
  parent?: string;
  position?: number;
  versionId?: number;
  visible?: boolean;
  titleSl?: any;
  definitionSl?: any;
  status?: string;
}

export class Concept implements IConcept {
  constructor(
    public id?: number,
    public uri?: string,
    public notation?: string,
    public title?: any,
    public definition?: any,
    public previousConcept?: number,
    public slConcept?: number,
    public parent?: string,
    public position?: number,
    public versionId?: number,
    public visible?: boolean,
    public titleSl?: any,
    public definitionSl?: any,
    public status?: string
  ) {
    this.parent = '';
    // used as flag for tree open collapse
    this.visible = true;
  }
}
