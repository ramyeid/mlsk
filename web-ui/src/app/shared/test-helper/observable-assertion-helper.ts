import { Observable } from 'rxjs';

export class ObservableAssertionHelper {

  private constructor() { }

  public static assertOnEmittedItems<T>(observable: Observable<T>, expectedValues: T[], done: DoneFn): void {
    let index = 0;

    observable.subscribe({
      next: (actualValue: T) => expect(actualValue).toEqual(expectedValues[index++]),
      error: () => expect(true).toBeFalse(),
      complete: () => {
        expect(index).toEqual(expectedValues.length);
        done();
      }
    });
  }

  public static assertOnNoEmittedItems<T>(observable: Observable<T>, done: DoneFn): void {
    observable.subscribe({
      next: () => expect(true).toBeFalse(),
      error: () => expect(true).toBeFalse(),
      complete: () => {
        expect().nothing();
        done();
      }
    });
  }

  public static assertOnEmittedError<T>(observable: Observable<T>, errorMessage: string, done: DoneFn): void {
    observable.subscribe({
      next: () => expect(true).toBeFalse(),
      error: (err: Error | string) => {
        const actualMessage: string = (err instanceof Error) ? err.message : err;
        expect(actualMessage).toEqual(errorMessage);
        done();
      },
      complete: () => expect(true).toBeFalse()
    });
  }
}
