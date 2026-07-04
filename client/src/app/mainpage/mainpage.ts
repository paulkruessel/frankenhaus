import { Component } from '@angular/core';
import { Navbar } from "../navbar/navbar";

@Component({
  selector: 'app-mainpage',
  imports: [Navbar],
  templateUrl: './mainpage.html',
  styleUrl: './mainpage.css',
})
export class Mainpage {}
