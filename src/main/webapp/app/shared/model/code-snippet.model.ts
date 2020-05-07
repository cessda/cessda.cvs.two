export interface ICodeSnippet {
  actionType?: string;
  versionId?: number;
  conceptId?: number;
  conceptSlId?: number;
  parent?: string;
  notation?: string;
  title?: any;
  definition?: any;
  position?: number;
  changeType?: any;
  changeDescription?: any;
  conceptStructures?: string[];
  conceptStructureIds?: number[];
}

export class CodeSnippet implements ICodeSnippet {
  constructor(
    public actionType?: string,
    public versionId?: number,
    public conceptId?: number,
    public conceptSlId?: number,
    public parent?: string,
    public notation?: string,
    public title?: any,
    public definition?: any,
    public position?: number,
    public changeType?: any,
    public changeDescription?: any,
    public conceptStructures?: string[],
    public conceptStructureIds?: number[]
  ) {}
}
