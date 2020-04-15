import { dataProviderResolver } from './dataProviderResolver';

const createDataProvider = (params = {}) => ({
  url: 'https://i-novus.ru/test/:id/:name',
  pathMapping: {
    id: {
      link: "models.resolve['proto_clients'].id",
    },
    name: {
      link: "models.resolve['proto_clients'].name",
    },
  },
  queryMapping: {
    param1: {
      value: '`param1`',
      link: "models.filter['proto_clients']",
    },
  },
  headersMapping: {
    TEST_HEADER: {
      value: 'TEST_HEADER_VALUE',
    },
    HEADER_BY_LINK: {
      link: "models.filter['proto_clients'].headerByLink",
    },
  },
  formMapping: {
    a: {
      link: 'models.resolve.form.a',
    },
    b: {
      link: 'models.resolve.form.b',
    },
  },
  ...params,
});

const state = {
  models: {
    resolve: {
      proto_clients: {
        id: 321,
        name: 'Michael-Jackson',
      },
      form: {
        a: 'a123',
        b: 'b321',
      },
    },
    filter: {
      proto_clients: {
        param1: 'param1Value',
        headerByLink: 'HEADER_BY_LINK_VALUE',
      },
    },
  },
};

const query = {};

const options = {};

describe('dataProviderResolver', () => {
  it('Должен вернуть все параметры', () => {
    const {
      basePath,
      baseQuery,
      headersParams,
      formParams,
      url,
    } = dataProviderResolver(state, createDataProvider(), query, options);

    expect(basePath).toBe('https://i-novus.ru/test/321/Michael-Jackson');
    expect(baseQuery).toEqual({ param1: 'param1Value' });
    expect(headersParams).toEqual({
      TEST_HEADER: 'TEST_HEADER_VALUE',
      HEADER_BY_LINK: 'HEADER_BY_LINK_VALUE',
    });
    expect(formParams).toEqual({ a: 'a123', b: 'b321' });
    expect(url).toBe(
      'https://i-novus.ru/test/321/Michael-Jackson?param1=param1Value'
    );
  });
});