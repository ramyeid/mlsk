import { Component, OnInit } from '@angular/core';

import { AdminService } from '../../service/admin.service';
import { EnginesDetail } from '../../model/engines-detail';

@Component({
  selector: 'mlsk-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  private readonly service: AdminService;
  enginesDetail: EnginesDetail;
  errorMessage: string;

  constructor(service: AdminService) {
    this.service = service;
    this.errorMessage = '';
  }

  ngOnInit(): void {
    this.service.ping().subscribe({
      next: (result: EnginesDetail) => this.enginesDetail = result,
      error: (err: Error) => this.errorMessage = err.message
    });
  }
}
