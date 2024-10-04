import { ColumnType } from "../enums/column.enum";
import { MerchantDto } from "./merchant-dto.model";

export class ColumnConfig {
    columnDef: ColumnType;
    header: string;
    cell: (element: MerchantDto) => string | number;
}