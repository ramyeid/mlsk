import { TestBed, ComponentFixture } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { InputListComponent } from './input-list.component';

describe('InputListComponent', () => {

  let component: InputListComponent;
  let fixture: ComponentFixture<InputListComponent>;
  const mockOnChange: jasmine.Spy = jasmine.createSpy();
  const mockOnTouched: jasmine.Spy = jasmine.createSpy();

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ FormsModule ],
      declarations: [ InputListComponent ]
    });

    fixture = TestBed.createComponent(InputListComponent);
    component = fixture.componentInstance;
    component.registerOnChange(mockOnChange);
    component.registerOnTouched(mockOnTouched);
    fixture.detectChanges();
  });


  describe('Write value', () => {

    it('should set item on write value', () => {

      component.writeValue('myValue');

      expect(component.currentItem).toEqual('myValue');
    });

  });


  describe('Modification in input', () => {

    it('should call onChange and onTouched on input modificiation', () => {
      const inputElement = fixture.debugElement.query(By.css('input')).nativeElement;

      ComponentHelper.setInputValue(fixture, 'item1');

      expect(component.value).toEqual(new Set());
      expect(mockOnChange).toHaveBeenCalledWith([]);
      expect(mockOnTouched).toHaveBeenCalled();
      expect(inputElement.value).toEqual('item1');
    });

  });


  describe('Adding Items', () => {

    it('should not add item if input is empty on add', () => {
      const items: string[] = [ '    ', '' ];

      ComponentHelper.setInputValueAndAdd(fixture, items);

      AssertionHelper.expectOnInputList(fixture, mockOnChange, mockOnTouched, []);
    });

    it('should add item in list and create item button on add with input', () => {
      const items: string[] = [ 'element1   ', '  element2  ' ];

      ComponentHelper.setInputValueAndAdd(fixture, items);

      AssertionHelper.expectOnInputList(fixture, mockOnChange, mockOnTouched, [ 'element1', 'element2' ]);
    });

    it('should not add duplicate', () => {
      const items: string[] = [ 'element1   ', '  element2  ', 'element1' ];

      ComponentHelper.setInputValueAndAdd(fixture, items);

      AssertionHelper.expectOnInputList(fixture, mockOnChange, mockOnTouched, [ 'element1', 'element2' ]);
    });

  });


  describe('Deleting Items', () => {

    it('should delete specific item', () => {
      const items: string[] = [ 'element1', 'element2', 'element3', 'element4' ];
      ComponentHelper.setInputValueAndAdd(fixture, items);

      ComponentHelper.deleteItemAt(fixture, 2);

      AssertionHelper.expectOnInputList(fixture, mockOnChange, mockOnTouched, [ 'element1', 'element2', 'element4' ]);
    });

    it('should delete all items', () => {
      const items: string[] = [ 'element1', 'element2', 'element3', 'element4' ];
      ComponentHelper.setInputValueAndAdd(fixture, items);

      ComponentHelper.deleteItemAt(fixture, 2);
      ComponentHelper.deleteItemAt(fixture, 0);
      ComponentHelper.deleteItemAt(fixture, 1);
      ComponentHelper.deleteItemAt(fixture, 0);

      AssertionHelper.expectOnInputList(fixture, mockOnChange, mockOnTouched, [ ]);
    });

  });

});

class ComponentHelper {

  private constructor() { }

  static setInputValue(fixture: ComponentFixture<InputListComponent>, item: string): void {
    const inputElement = fixture.debugElement.query(By.css('input')).nativeElement;

    inputElement.value = item;
    inputElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
  }

  static setInputValueAndAdd(fixture: ComponentFixture<InputListComponent>, items: string[]): void {
    const addButton = fixture.debugElement.query(By.css('#addItemButton'));

    for(let i = 0; i < items.length; ++i) {
      this.setInputValue(fixture, items[i]);
      addButton.triggerEventHandler('click', null);
      fixture.detectChanges();
    }
  }

  static deleteItemAt(fixture: ComponentFixture<InputListComponent>, index: number): void {
    const rmButton = fixture.debugElement.queryAll(By.css('#rmItemButton'))[index];

    rmButton.triggerEventHandler('click', null);
    fixture.detectChanges();
  }

}

class AssertionHelper {

  private constructor() { }

  static expectOnInputList(fixture: ComponentFixture<InputListComponent>, mockOnChanged: jasmine.Spy, mockOnTouched: jasmine.Spy, expectedItems: string[]): void {
    expect(fixture.componentInstance.value).toEqual(new Set(expectedItems));

    expect(mockOnChanged).toHaveBeenCalledWith(expectedItems);
    expect(mockOnTouched).toHaveBeenCalled();

    const itemButtons  = fixture.debugElement.queryAll(By.css('#rmItemButton'));
    expect(itemButtons.length).toEqual(expectedItems.length);
    for (let i = 0; i < expectedItems.length; i++) {
      expect(itemButtons[i].nativeElement.textContent).toEqual(' ' +  expectedItems[i] + '  | x');
    }
  }
}
