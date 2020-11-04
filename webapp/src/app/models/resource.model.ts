
export class Resource {
    name: string;
    value: number;
    maxValue: number;
    mappedValue: number;

    constructor(
        name: string = '',
        maxValue: number = 100,
        value: number = 1
    ) {
        this.name = name;
        this.value = value;
        this.maxValue = maxValue;
    }
}
