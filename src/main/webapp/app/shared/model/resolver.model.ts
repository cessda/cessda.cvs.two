import { ResourceType } from 'app/shared/model/enumerations/resource-type.model';
import { ResolverType } from 'app/shared/model/enumerations/resolver-type.model';

export interface IResolver {
  id?: number;
  resourceId?: string;
  resourceType?: ResourceType;
  resourceUrl?: string;
  resolverType?: ResolverType;
  resolverURI?: string;
}

export class Resolver implements IResolver {
  constructor(
    public id?: number,
    public resourceId?: string,
    public resourceType?: ResourceType,
    public resourceUrl?: string,
    public resolverType?: ResolverType,
    public resolverURI?: string
  ) {}
}
