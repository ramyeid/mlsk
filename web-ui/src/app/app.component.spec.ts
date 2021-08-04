import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppComponent } from './app.component';

describe('AppComponent', () => {

  let fixture: ComponentFixture<AppComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ AppComponent ],
      schemas: [ NO_ERRORS_SCHEMA ]
    });
    fixture = TestBed.createComponent(AppComponent);
  });

  it('should have as machine-learning-swissknife title', () => {
    fixture = TestBed.createComponent(AppComponent);

    const actualTitle = fixture.componentInstance.title;

    expect(actualTitle).toEqual('machine-learning-swissknife');
  });

});
