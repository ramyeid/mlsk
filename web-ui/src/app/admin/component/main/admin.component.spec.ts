import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminComponent } from './admin.component';

describe('AdminComponent', () => {
  let admin: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminComponent]
    });
    fixture = TestBed.createComponent(AdminComponent);
    admin = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(admin).toBeTruthy();
  });
});
