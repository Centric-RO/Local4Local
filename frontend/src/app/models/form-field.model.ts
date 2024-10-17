import { CategoryDto } from "../_models/category-dto.model";

export class FormField {
    public formControl: string;
    public labelKey: string;
    public fieldType: 'input' | 'select' | 'textarea';
    public required: boolean;
    public isReadOnly: boolean;
    public options?: CategoryDto[];
    public requiredMessage?: string;
    public pattern?: string;
    public patternMessage?: string;
    public maxLength?: number | null;
}