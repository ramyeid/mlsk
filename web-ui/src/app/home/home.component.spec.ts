import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponent } from './home.component';

describe('HomeComponent', () => {

  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ HomeComponent ],
    });
    fixture = TestBed.createComponent(HomeComponent);
  });


  it('should render page with correct information', () => {

    expect(fixture.nativeElement.querySelectorAll('p')[0].textContent).toEqual('home works!');
    expect(fixture.nativeElement.querySelectorAll('p')[1].textContent).toEqual('TODO: explanation about the project');
  });

});
