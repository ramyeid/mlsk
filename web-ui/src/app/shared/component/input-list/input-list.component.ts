import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'mlsk-input-list',
  templateUrl: './input-list.component.html',
  styleUrls: ['./input-list.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => InputListComponent),
      multi: true
    }
  ]
})
export class InputListComponent implements ControlValueAccessor {

  @Input() placeholder: string;
  @Input() id: string;
  @Input() errorMessage: string | undefined | null;
  currentItem: string;
  value: Set<string> = new Set();
  private onChange: (value: string[]) => void;
  private onTouched: () => void;

  addItem(): void {
    if (this.currentItem.trim() != '') {
      this.value.add(this.currentItem.trim());
      this.currentItem = '';
    }

    this.onModification();
  }

  onModification(): void {
    this.onChange(Array.from(this.value.values()));
    this.onTouched();
  }

  deleteItem(item: string): void {
    this.value.delete(item);
    this.onModification();
  }

  writeValue(value: string): void {
    this.currentItem = value;
  }

  registerOnChange(fn: (value: string[]) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

}
