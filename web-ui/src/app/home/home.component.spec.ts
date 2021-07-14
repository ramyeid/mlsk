import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

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

    expect(fixture.debugElement.queryAll(By.css('p'))[0].nativeElement.textContent).toEqual('home works!');
    expect(fixture.debugElement.queryAll(By.css('p'))[1].nativeElement.textContent).toEqual('TODO: explanation about the project');
  });

});
