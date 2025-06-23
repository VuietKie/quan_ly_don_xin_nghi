export const maxValueByColumn = (
  data: any[] = [],
  groupByColumn: string,
  valueColumn: string
): { name: string; value: number }[] => {
  const result: Record<string, number> = {};

  data.forEach((item) => {
    const groupKey = item[groupByColumn];
    const value = Number(item[valueColumn]);

    if (!groupKey || isNaN(value)) return;

    if (!(groupKey in result) || value > result[groupKey]) {
      result[groupKey] = value;
    }
  });

  return Object.entries(result).map(([name, value]) => ({ name, value }));
};

export const maxByMultiColumn = (
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
      groupedData[groupKey][valueColumn] = -Infinity;
    }

    groupedData[groupKey][valueColumn] = Math.max(groupedData[groupKey][valueColumn], value);
  });

  const result: any[][] = [headers];
  Object.values(groupedData).forEach((groupedItem) => {
    const row = headers.map((header) => groupedItem[header]);
    result.push(row);
  });

  return result;
};