export const countByColumn = (
  data: any[] = [],
  columnName: string
): { name: string; value: number }[] => {
  const result: Record<string, number> = {};
  data.forEach((item) => {
    const key = item[columnName];
    if (!key) return;

    if (!result[key]) {
      result[key] = 0;
    }
    result[key] += 1;
  });
  
  const dataConvert = Object.entries(result).map(([name, value]) => ({
    name,
    value,
  }));
  
  return dataConvert;
};

export const countByMultiColumn = (
  data: any[] = [],
  groupByColumns: string[],
  headers: string[]
): any[][] => {
  const groupedData: Record<string, any> = {};

  data.forEach((item) => {
    const groupKey = groupByColumns.map((col) => item[col]).join("|");

    if (!groupKey) return;

    if (!groupedData[groupKey]) {
      groupedData[groupKey] = groupByColumns.reduce((acc, col) => {
        acc[col] = item[col];
        return acc;
      }, {} as Record<string, any>);
      groupedData[groupKey].count = 0;
    }

    groupedData[groupKey].count += 1;
  });
  console.log(groupedData)
  const result: any[][] = [headers];
  Object.values(groupedData).forEach((groupedItem) => {
    const row = headers.map((header) => groupedItem[header]);
    result.push(row);
  });

  return result;
};