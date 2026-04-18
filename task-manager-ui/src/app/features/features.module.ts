import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Profile } from './profile/profile';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [
    Profile
  ],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class FeaturesModule { }
