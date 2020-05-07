export interface IBucket {
  k?: string; // key -> keyword
  v?: number; // value -> number of documents
  value?: string;
  display?: string;
  readonly?: boolean;
}

export class Bucket implements IBucket {
  constructor(public k?: string, public v?: number, public value?: string, public display?: string, public readonly?: boolean) {}
}
