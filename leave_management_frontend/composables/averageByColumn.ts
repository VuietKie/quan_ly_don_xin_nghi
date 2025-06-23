export const averageByColumn = (
  data: any[] = [],
  groupByColumn: string,
  valueColumn: string
): { name: string; value: number }[] => {
  const sumResult: Record<string, number> = {};
  const countResult: Record<string, number> = {};

  data.forEach((item) => {
    const groupKey = item[groupByColumn];
    const value = Number(item[valueColumn]);

    if (!groupKey || isNaN(value)) return;

    if (!sumResult[groupKey]) {
      sumResult[groupKey] = 0;
      countResult[groupKey] = 0;
    }
    sumResult[groupKey] += value;
    countResult[groupKey] += 1;
  });

  const averageResult = Object.keys(sumResult).map((key) => ({
    name: key,
    value: sumResult[key] / countResult[key],
  }));

  return averageResult;
};

export const averageByMultiColumn = (
  data: any[] = [],
  groupByColumns: string[],
  valueColumn: string,
  headers: string[]
): any[][] => {
  const groupedData: Record<string, any> = {};

  data.forEach((item) => {
    const groupKey = groupByColumns.map((col) => item[col]).join("|");
    const value = Number(item[valueColumn]);

    if (!groupKey || isNaN(value)) return;

    if (!groupedData[groupKey]) {
      groupedData[groupKey] = groupByColumns.reduce((acc, col) => {
        acc[col] = item[col];
        return acc;
      }, {} as Record<string, any>);
      groupedData[groupKey][valueColumn] = 0;
      groupedData[groupKey].count = 0;
    }

    groupedData[groupKey][valueColumn] += value;
    groupedData[groupKey].count += 1;
  });

  const result: any[][] = [headers];
  Object.values(groupedData).forEach((groupedItem) => {
    groupedItem[valueColumn] = groupedItem[valueColumn] / groupedItem.count;
    delete groupedItem.count;

    const row = headers.map((header) => groupedItem[header]);
    result.push(row);
  });

  return result;
};
